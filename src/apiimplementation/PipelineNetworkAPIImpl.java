package apiimplementation;

import apiengine.ConceptualAPI;
import apiengine.NetworkAPI;
import apinetwork.ComputationInput;
import apinetwork.ComputationOutput;
import apinetwork.JobRequest;
import apistorage.ProcessAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class PipelineNetworkAPIImpl implements NetworkAPI {

    // Existing core components
    private final ProcessAPI processAPI;
    private final ConceptualAPI conceptual;

    // Thread pool for computation
    private final ExecutorService computePool;

    // Queue for inputs waiting to be computed
    private final BlockingQueue<WorkItem> inputQueue;

    // Queue for results waiting to be written
    private final BlockingQueue<ResultItem> outputQueue;

    private final int numWorkers;

    // Default configuration
    public PipelineNetworkAPIImpl(ProcessAPI processAPI, ConceptualAPI conceptual) {
        this(processAPI, conceptual, 4, 256, 256);
    }

    // Fully configurable constructor
    public PipelineNetworkAPIImpl(
            ProcessAPI processAPI,
            ConceptualAPI conceptual,
            int numWorkers,
            int inputQueueCapacity,
            int outputQueueCapacity) {

        if (processAPI == null || conceptual == null) {
            throw new IllegalArgumentException("processAPI and conceptual must not be null");
        }

        this.processAPI = processAPI;
        this.conceptual = conceptual;
        this.numWorkers = Math.max(1, numWorkers);

        // Fixed-size pool for CPU work
        this.computePool = Executors.newFixedThreadPool(this.numWorkers);

        // Bounded queues prevent unlimited memory growth
        this.inputQueue = new ArrayBlockingQueue<>(Math.max(1, inputQueueCapacity));
        this.outputQueue = new ArrayBlockingQueue<>(Math.max(1, outputQueueCapacity));
    }

    // Optional cleanup
    public void shutdown() {
        computePool.shutdown();
    }

    @Override
    public ComputationOutput sendJob(JobRequest job) {

        // Preserve existing behavior for null jobs
        if (job == null) {
            try {
                processAPI.writeOutput("network:null-job");
            } catch (Throwable t) {
                // Ignore failure to preserve existing behavior
            }
            return new ComputationOutput("invalid-job");
        }

        // Batch jobs use the pipeline
        if (job.getInputNumber() == -1) {
            return runBatchPipeline(job);
        }

        // Single jobs delegate to the existing implementation
        NetworkAPIImpl delegate = new NetworkAPIImpl(processAPI, conceptual);
        return delegate.sendJob(job);
    }

    private ComputationOutput runBatchPipeline(JobRequest job) {

        List<Integer> inputs;
        try {
            inputs = processAPI.readInputs();
        } catch (Throwable t) {
            safeWrite("batch:read-error");
            return new ComputationOutput("batch:error");
        }

        if (inputs == null || inputs.isEmpty()) {
            safeWrite("batch:no-inputs");
            return new ComputationOutput("batch:empty");
        }

        int total = inputs.size();
        AtomicBoolean failed = new AtomicBoolean(false);

        // Writer thread ensures outputs are written in input order
        Thread writer = new Thread(() -> {
            int nextIndex = 0;
            Map<Integer, String> buffer = new HashMap<>();

            while (nextIndex < total && !failed.get()) {
                try {
                    ResultItem r = outputQueue.take();
                    buffer.put(r.index, r.value);

                    while (buffer.containsKey(nextIndex)) {
                        boolean ok = safeWrite(buffer.remove(nextIndex));
                        if (!ok) {
                            failed.set(true);
                            safeWrite("batch:write-failure");
                            return;
                        }
                        nextIndex++;
                    }
                } catch (InterruptedException e) {
                    // Restore interrupt status and mark pipeline as failed
                    Thread.currentThread().interrupt();
                    failed.set(true);
                }
            }
        });

        writer.start();

        // Start compute workers
        CountDownLatch workersDone = new CountDownLatch(numWorkers);
        for (int i = 0; i < numWorkers; i++) {
            computePool.submit(() -> {
                try {
                    while (true) {
                        WorkItem item = inputQueue.take();
                        if (item.poison) {
                            return;
                        }

                        ComputationOutput out;
                        try {
                            out = conceptual.compute(
                                    new ComputationInput(item.value, job.getDelimiters()));
                        } catch (Throwable t) {
                            out = new ComputationOutput("error:compute-exception");
                        }

                        String result =
                                (out == null || out.getResult() == null) ? "null" : out.getResult();

                        outputQueue.put(new ResultItem(item.index, result));
                    }
                } catch (InterruptedException e) {
                    // Worker interrupted while waiting for work
                    Thread.currentThread().interrupt();
                    failed.set(true);
                } finally {
                    workersDone.countDown();
                }
            });
        }

        // Producer feeds inputs into the pipeline
        try {
            for (int i = 0; i < total; i++) {
                inputQueue.put(new WorkItem(i, inputs.get(i), false));
            }
            for (int i = 0; i < numWorkers; i++) {
                inputQueue.put(WorkItem.POISON);
            }
        } catch (InterruptedException e) {
            // Interrupted while enqueueing work
            Thread.currentThread().interrupt();
            failed.set(true);
        }

        // Wait for workers and writer
        try {
            workersDone.await(10, TimeUnit.SECONDS);
            writer.join(10_000);
        } catch (InterruptedException e) {
            // Interrupted while waiting for completion
            Thread.currentThread().interrupt();
            failed.set(true);
        }

        if (failed.get()) {
            return new ComputationOutput("batch:error");
        }

        String completed = "batch:completed:" + total;
        safeWrite(completed);
        return new ComputationOutput(completed);
    }

    private boolean safeWrite(String s) {
        try {
            return processAPI.writeOutput(s);
        } catch (Throwable t) {
            return false;
        }
    }

    // Work item sent to compute workers
    private static class WorkItem {
        final int index;
        final int value;
        final boolean poison;

        static final WorkItem POISON = new WorkItem(-1, 0, true);

        WorkItem(int index, int value, boolean poison) {
            this.index = index;
            this.value = value;
            this.poison = poison;
        }
    }

    // Result item sent to writer
    private static class ResultItem {
        final int index;
        final String value;

        ResultItem(int index, String value) {
            this.index = index;
            this.value = value;
        }
    }
}

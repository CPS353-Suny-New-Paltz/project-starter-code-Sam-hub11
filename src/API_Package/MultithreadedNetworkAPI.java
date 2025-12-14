package API_Package;

import apiengine.NetworkAPI;
import apinetwork.JobRequest;
import apinetwork.ComputationOutput;
import apinetwork.ComputationInput;
import apinetwork.Delimiters;
import apistorage.ProcessAPI;
import apiengine.ConceptualAPI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MultithreadedNetworkAPI implements NetworkAPI {

    private final ProcessAPI processAPI;
    private final ConceptualAPI conceptual;
    private final ExecutorService pool;
    private final int maxThreads;

    // default constructor: 4 threads, no backing ProcessAPI/conceptual
    public MultithreadedNetworkAPI() {
        this(null, null, 4);
    }

    // main constructor
    public MultithreadedNetworkAPI(ProcessAPI processAPI, ConceptualAPI conceptual, int maxThreads) {
        this.processAPI = processAPI;
        this.conceptual = conceptual;
        this.maxThreads = Math.max(1, maxThreads);
        this.pool = Executors.newFixedThreadPool(this.maxThreads);
    }

    // convenience ctor
    public MultithreadedNetworkAPI(ProcessAPI processAPI, ConceptualAPI conceptual) {
        this(processAPI, conceptual, 4);
    }

    /**
     * Shutdown the internal thread pool. Tests should call this after runs that used this instance.
     */
    public void shutdown() {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(2, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public ComputationOutput sendJob(JobRequest job) {
        if (job == null) {
            try {
                if (processAPI != null) processAPI.writeOutput("network:null-job");
            } catch (Throwable ignored) {}
            return new ComputationOutput("invalid-job");
        }

        if (processAPI == null || conceptual == null) {
            return new ComputationOutput("error:network-not-configured");
        }

        try {
            // Batch mode
            if (job.getInputNumber() == -1) {
                List<Integer> inputs;
                try {
                    inputs = processAPI.readInputs();
                } catch (Throwable t) {
                    try { processAPI.writeOutput("batch:read-error"); } catch (Throwable ignored) {}
                    return new ComputationOutput("batch:error");
                }

                if (inputs == null || inputs.isEmpty()) {
                    try { processAPI.writeOutput("batch:no-inputs"); } catch (Throwable ignored) {}
                    return new ComputationOutput("batch:empty");
                }

                List<Future<ComputationOutput>> futures = new ArrayList<>();
                for (Integer v : inputs) {
                    final int value = v;
                    final Delimiters delims = job.getDelimiters();
                    futures.add(pool.submit(() -> {
                        ComputationInput ci = new ComputationInput(value, delims);
                        return conceptual.compute(ci);
                    }));
                }

                int processed = 0;
                for (Future<ComputationOutput> f : futures) {
                    ComputationOutput out;
                    try {
                        out = f.get();
                    } catch (ExecutionException ee) {
                        try { processAPI.writeOutput("batch:task-failure"); } catch (Throwable ignored) {}
                        return new ComputationOutput("batch:error");
                    }
                    String s = (out == null || out.getResult() == null) ? "null" : out.getResult();
                    try {
                        processAPI.writeOutput(s);
                    } catch (Throwable t) {
                        try { processAPI.writeOutput("batch:write-failure"); } catch (Throwable ignored) {}
                        return new ComputationOutput("batch:error");
                    }
                    processed++;
                }

                try { processAPI.writeOutput("batch:completed:" + processed); } catch (Throwable ignored) {}
                return new ComputationOutput("batch:success");
            }

            // Single job validation
            if (job.getInputNumber() < 0) {
                try { processAPI.writeOutput("network:invalid-input-number"); } catch (Throwable ignored) {}
                return new ComputationOutput("invalid-job");
            }

            Future<ComputationOutput> future = pool.submit(() -> {
                ComputationInput ci = new ComputationInput(job.getInputNumber(), job.getDelimiters());
                return conceptual.compute(ci);
            });

            ComputationOutput out;
            try {
                out = future.get();
            } catch (ExecutionException ee) {
                try { processAPI.writeOutput("network:task-failure"); } catch (Throwable ignored) {}
                return new ComputationOutput("error:network-exception:" + (ee.getMessage() == null ? "unknown" : ee.getMessage()));
            }

            String s = (out == null || out.getResult() == null) ? "null" : out.getResult();
            try {
                processAPI.writeOutput(s);
            } catch (Throwable t) {
                try { processAPI.writeOutput("network:write-failure"); } catch (Throwable ignored) {}
                return new ComputationOutput("error:write-failed");
            }
            return out;
        } catch (Exception ex) {
            String msg = ex.getMessage() == null ? "unknown" : ex.getMessage();
            try { processAPI.writeOutput("network:error:" + msg); } catch (Throwable ignored) {}
            return new ComputationOutput("error:network-exception:" + msg);
        }
    }
}

package api_package;

import apiengine.NetworkAPI;
import apinetwork.JobRequest;
import apinetwork.ComputationOutput;
import apistorage.ProcessAPI;
import apiengine.ConceptualAPI;
import apiimplementation.NetworkAPIImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Multithreaded wrapper around NetworkAPIImpl.
 *
 * This class does NOT change computation semantics.
 * It simply runs sendJob asynchronously using a thread pool.
 */
public class MultithreadedNetworkAPI implements NetworkAPI {

    private final ExecutorService executor;
    private final NetworkAPIImpl delegate;

    /**
     * Default constructor required by TestMultiUser.
     * Uses a dummy executor; real work is done in TestUser.
     */
    public MultithreadedNetworkAPI() {
        this.executor = Executors.newFixedThreadPool(4);
        this.delegate = null;
    }

    /**
     * Constructor used by TestUser.
     */
    public MultithreadedNetworkAPI(
            ProcessAPI processAPI,
            ConceptualAPI conceptual,
            int maxthreads) {

        this.executor = Executors.newFixedThreadPool(Math.max(1, maxthreads));
        this.delegate = new NetworkAPIImpl(processAPI, conceptual);
    }

    @Override
    public ComputationOutput sendJob(JobRequest job) {
        if (delegate == null) {
            return new ComputationOutput("error:network-not-configured");
        }

        try {
            return executor.submit(() -> delegate.sendJob(job)).get();
        } catch (Exception e) {
            return new ComputationOutput("error:network-exception");
        }
    }

    /**
     * Required by TestUser and TestMultiUser.
     */
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

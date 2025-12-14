package API_Package;

import apiengine.NetworkAPI;
import apiengine.ConceptualAPI;
import apinetwork.ComputationOutput;
import apinetwork.JobRequest;
import apistorage.ProcessAPI;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Multi-threaded NetworkAPI implementation.
 *
 * This class delegates computation logic to the existing
 * single-threaded NetworkAPIImpl, but executes jobs using
 * a fixed-size thread pool.
 */
public class MultithreadedNetworkAPI implements NetworkAPI
{
    private static final int MAX_THREADS = 4;

    private final ExecutorService executor;
    private final NetworkAPI delegate;

    public MultithreadedNetworkAPI(ProcessAPI processAPI,
                                   ConceptualAPI conceptualAPI)
    {
        this.executor = Executors.newFixedThreadPool(MAX_THREADS);
        this.delegate = new apiimplementation.NetworkAPIImpl(
                processAPI,
                conceptualAPI
        );
    }

    @Override
    public ComputationOutput sendJob(JobRequest job)
    {
        if (job == null)
        {
            return new ComputationOutput("network:null-job");
        }

        executor.submit(() ->
        {
            try
            {
                delegate.sendJob(job);
            }
            catch (Exception e)
            {
                System.err.println(
                        "[MultithreadedNetworkAPI] Error: " + e.getMessage()
                );
            }
        });

        return new ComputationOutput("network:accepted");
    }

    public void shutdown()
    {
        executor.shutdown();

        try
        {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES))
            {
                executor.shutdownNow();
            }
        }
        catch (InterruptedException e)
        {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

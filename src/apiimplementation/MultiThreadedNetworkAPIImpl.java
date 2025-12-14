package apiimplementation;

import apiengine.NetworkAPI;
import apinetwork.ComputationOutput;
import apinetwork.JobRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Multi-threaded NetworkAPI implementation.
 *
 * Delegates all logic to NetworkAPIImpl, but runs the job on a thread pool.
 */
public class MultiThreadedNetworkAPIImpl implements NetworkAPI {

    private final ExecutorService executor;
    private final NetworkAPIImpl delegate;

    public MultiThreadedNetworkAPIImpl(
            ExecutorService executor,
            NetworkAPIImpl delegate) {

        if (executor == null || delegate == null) {
            throw new IllegalArgumentException("executor and delegate must not be null");
        }
        this.executor = executor;
        this.delegate = delegate;
    }

    @Override
    public ComputationOutput sendJob(JobRequest job) {
        try {
            Future<ComputationOutput> future =
                    executor.submit(() -> delegate.sendJob(job));
            return future.get();
        } catch (Exception e) {
            System.err.println("[MultiThreadedNetworkAPIImpl] " + e.getMessage());
            return new ComputationOutput("error:multithreaded-failure");
        }
    }
}

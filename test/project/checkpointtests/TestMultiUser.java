package project.checkpointtests;

import api_package.MultithreadedNetworkAPI;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestMultiUser {

    private MultithreadedNetworkAPI networkAPI;

    @BeforeEach
    public void initializeComputeEngine() {
        networkAPI = new MultithreadedNetworkAPI();
    }

    public void cleanup() {
        if (networkAPI != null) {
            networkAPI.shutdown();
        }
    }

    @Test
    public void compareMultiAndSingleThreaded() throws Exception {
        int nthreads = 4;
        List<TestUser> testUsers = new ArrayList<>();

        for (int i = 0; i < nthreads; i++) {
            testUsers.add(new TestUser());
        }

        String singleThreadFilePrefix =
                "testMultiUser.compareMultiAndSingleThreaded.test.singleThreadOut.tmp";

        for (int i = 0; i < nthreads; i++) {
            File singleThreadedOut = new File(singleThreadFilePrefix + i);
            singleThreadedOut.deleteOnExit();
            testUsers.get(i).run(singleThreadedOut.getCanonicalPath(), false);
        }

        ExecutorService threadPool = Executors.newCachedThreadPool();
        List<Future<?>> results = new ArrayList<>();

        String multiThreadFilePrefix =
                "testMultiUser.compareMultiAndSingleThreaded.test.multiThreadOut.tmp";

        for (int i = 0; i < nthreads; i++) {
            File multiThreadedOut = new File(multiThreadFilePrefix + i);
            multiThreadedOut.deleteOnExit();
            String multiThreadOutputPath = multiThreadedOut.getCanonicalPath();
            TestUser testUser = testUsers.get(i);

            results.add(threadPool.submit(() ->
                    testUser.run(multiThreadOutputPath, true)));
        }

        for (Future<?> future : results) {
            future.get();
        }

        List<String> singleThreaded = loadAllOutput(singleThreadFilePrefix, nthreads);
        List<String> multiThreaded = loadAllOutput(multiThreadFilePrefix, nthreads);

        Assert.assertEquals(singleThreaded, multiThreaded);
    }

    private List<String> loadAllOutput(String prefix, int nthreads) throws IOException {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < nthreads; i++) {
            File out = new File(prefix + i);
            result.addAll(Files.readAllLines(out.toPath()));
        }
        return result;
    }
}

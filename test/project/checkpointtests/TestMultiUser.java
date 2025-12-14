package project.checkpointtests;

import API_Package.MultithreadedNetworkAPI;

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
	
	// TODO 1: change the type of this variable to the name you're using for your @NetworkAPI
	// interface

	private MultithreadedNetworkAPI networkAPI;
	
	@BeforeEach
	public void initializeComputeEngine() {
		networkAPI = new MultithreadedNetworkAPI();
		//TODO 2: create an instance of the implementation of your @NetworkAPI; this is the component
		// that the user will make requests to
		// Store it in the 'coordinator' instance variable

		// We leave coordinator null here. Each TestUser.run constructs
		// its own coordinator instances (single-threaded or multithreaded)
		// wired to per-run ProcessAPIFileImpl so that each run writes
		// to an independent output file. This avoids global file races.
	}

	public void cleanup() {
        if (networkAPI != null) {
            networkAPI.shutdown();
        }
    }

	@Test
	public void compareMultiAndSingleThreaded() throws Exception {
		int nThreads = 4;
		List<TestUser> testUsers = new ArrayList<>();

		for (int i = 0; i < nThreads; i++) {
			testUsers.add(new TestUser());
		}
		
		// Run single threaded
		String singleThreadFilePrefix =
				"testMultiUser.compareMultiAndSingleThreaded.test.singleThreadOut.tmp";

		for (int i = 0; i < nThreads; i++) {
			File singleThreadedOut = new File(singleThreadFilePrefix + i);
			singleThreadedOut.deleteOnExit();
			testUsers.get(i).run(singleThreadedOut.getCanonicalPath(), false);
		}
		
		// Run multi threaded
		ExecutorService threadPool = Executors.newCachedThreadPool();
		List<Future<?>> results = new ArrayList<>();

		String multiThreadFilePrefix =
				"testMultiUser.compareMultiAndSingleThreaded.test.multiThreadOut.tmp";

		for (int i = 0; i < nThreads; i++) {
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
		
		// Check that the output is the same for multi-threaded and single-threaded
		List<String> singleThreaded = loadAllOutput(singleThreadFilePrefix, nThreads);
		List<String> multiThreaded = loadAllOutput(multiThreadFilePrefix, nThreads);

		Assert.assertEquals(singleThreaded, multiThreaded);
	}

	private List<String> loadAllOutput(String prefix, int nThreads) throws IOException {
		List<String> result = new ArrayList<>();
		for (int i = 0; i < nThreads; i++) {
			File out = new File(prefix + i);
			result.addAll(Files.readAllLines(out.toPath()));
		}
		return result;
	}
}

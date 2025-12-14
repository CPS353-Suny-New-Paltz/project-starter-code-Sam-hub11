package project.checkpointtests;

import apiengine.NetworkAPI;
import apinetwork.Delimiters;
import apinetwork.JobRequest;

import apistorage.ProcessAPI;
import apistorage.ProcessAPIFileImpl;

import apiimplementation.ConceptualAPIImpl;
import apiimplementation.NetworkAPIImpl;
import API_Package.MultithreadedNetworkAPI;

import java.io.File;

/**
 * TestUser: harness helper that executes a batch job using either a single-threaded
 * NetworkAPIImpl or the MultithreadedNetworkAPI, writing outputs to outputPath.
 *
 * (Professor TODO comments preserved in this file)
 */
public class TestUser {
	
	// TODO 3: change the type of this variable to the name you're using for your
	// @NetworkAPI interface; also update the parameter passed to the constructor
	// private final ComputationCoordinator coordinator;
	// We'll not store a long-lived coordinator here; each run will create its own one
	// wired to its own ProcessAPIFileImpl (so outputs go to the per-run file).
	
	public TestUser() {
	}

	// keep original signature for compatibility
	public void run(String outputPath) {
		run(outputPath, false);
	}

	// Overloaded run that chooses single vs multi-threaded implementation.
	public void run(String outputPath, boolean useMultithreaded) {
		char delimiter = ';';
		String inputPath = "test" + File.separatorChar + "testInputFile.test";
		
		// TODO 4: Call the appropriate method(s) on the coordinator to get it to 
		// run the compute job specified by inputPath, outputPath, and delimiter
		// IMPLEMENTATION BELOW (does not remove the professor comment above)

		ProcessAPI store = new ProcessAPIFileImpl(inputPath, outputPath);
		ConceptualAPIImpl conceptual = new ConceptualAPIImpl();

		NetworkAPI coordinator;

		if (useMultithreaded) {
			coordinator = new MultithreadedNetworkAPI(store, conceptual, 4);
		} else {
			coordinator = new NetworkAPIImpl(store, conceptual);
		}

		JobRequest req =
				new JobRequest(-1, new Delimiters(String.valueOf(delimiter), " × "));

		coordinator.sendJob(req);

		if (coordinator instanceof MultithreadedNetworkAPI) {
			((MultithreadedNetworkAPI) coordinator).shutdown();
		}
	}
}

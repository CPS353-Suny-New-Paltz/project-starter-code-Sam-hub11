package project.checkpointtests;

import apiengine.NetworkAPI;
import apinetwork.Delimiters;
import apinetwork.JobRequest;
import apipackage.MultithreadedNetworkAPI;
import apistorage.ProcessAPI;
import apistorage.ProcessAPIFileImpl;

import apiimplementation.ConceptualAPIImpl;
import apiimplementation.NetworkAPIImpl;

import java.io.File;

public class TestUser {

    public TestUser() {
    }

    public void run(String outputPath) {
        run(outputPath, false);
    }

    public void run(String outputPath, boolean useMultithreaded) {
        char delimiter = ';';
        String inputPath = "test" + File.separatorChar + "testInputFile.test";

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

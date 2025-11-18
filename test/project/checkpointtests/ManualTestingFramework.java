package project.checkpointtests;

import java.util.ArrayList;
import java.util.List;

import apiimplementation.ConceptualAPIImpl;
import apiimplementation.NetworkAPIImpl;
import apinetwork.Delimiters;
import apinetwork.JobRequest;
import testhelpers.TestInputConfig;
import testhelpers.TestOutputConfig;
import testhelpers.TestProcessDataStore;

public class ManualTestingFramework {

    // Parses raw string lines into integers, skipping blank and malformed entries.
    public static List<Integer> parseInputLines(List<String> rawLines) {
        List<Integer> ints = new ArrayList<>();
        if (rawLines == null) return ints;
        for (String s : rawLines) {
            if (s == null) continue;
            String t = s.trim();
            if (t.isEmpty()) continue;
            try {
                ints.add(Integer.parseInt(t));
            } catch (NumberFormatException nfe) {
                System.err.println("[ManualTestingFramework] skipping non-int: " + t);
            }
        }
        return ints;
    }

    // Wires up test store, conceptual engine, and network coordinator and runs a batch job.
    public static List<String> runManualNetworkBatch(List<Integer> inputs) {
        TestInputConfig inputConfig = new TestInputConfig(inputs);
        TestOutputConfig outputConfig = new TestOutputConfig();
        TestProcessDataStore testStore = new TestProcessDataStore(inputConfig, outputConfig);

        ConceptualAPIImpl conceptual = new ConceptualAPIImpl();
        NetworkAPIImpl network = new NetworkAPIImpl(testStore, conceptual);

        JobRequest job = new JobRequest(-1, new Delimiters(":", " × "));
        network.sendJob(job);

        return outputConfig.getOutputs();
    }

    // Convenience helper: parse raw lines and run batch.
    public static List<String> runManualNetworkBatchFromRawLines(List<String> rawLines) {
        return runManualNetworkBatch(parseInputLines(rawLines));
    }
}

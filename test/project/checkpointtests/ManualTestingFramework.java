package project.checkpointtests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class ManualTestingFramework {

    // Constants expected by Checkpoint4TestSuite
    public static final String INPUT = "input.data";
    public static final String OUTPUT = "output.data";

    // Parses raw string lines into integers, skipping blank and malformed entries.
    public static List<Integer> parseInputLines(List<String> rawLines) {
        java.util.List<Integer> ints = new java.util.ArrayList<>();
        if (rawLines == null) {
            return ints;
        }
        for (String s : rawLines) {
            if (s == null) {
                continue;
            }
            String t = s.trim();
            if (t.isEmpty()) {
                continue;
            }
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
        testhelpers.TestInputConfig inputConfig = new testhelpers.TestInputConfig(inputs);
        testhelpers.TestOutputConfig outputConfig = new testhelpers.TestOutputConfig();
        testhelpers.TestProcessDataStore testStore = new testhelpers.TestProcessDataStore(inputConfig, outputConfig);

        apiimplementation.ConceptualAPIImpl conceptual = new apiimplementation.ConceptualAPIImpl();
        apiimplementation.NetworkAPIImpl network = new apiimplementation.NetworkAPIImpl(testStore, conceptual);

        apinetwork.JobRequest job = new apinetwork.JobRequest(-1, new apinetwork.Delimiters(":", " × "));
        network.sendJob(job);

        return outputConfig.getOutputs();
    }

    // Convenience helper: parse raw lines and run batch.
    public static List<String> runManualNetworkBatchFromRawLines(List<String> rawLines) {
        return runManualNetworkBatch(parseInputLines(rawLines));
    }


    public static void main(String[] args) throws IOException {
        // Example input lines (the integration tests typically use similar small lists)
        List<String> sampleInputLines = Arrays.asList("1", "10", "25");

        Path inputPath = Paths.get(INPUT);
        // ensure parent dirs exist (if any) and write the input file
        if (inputPath.getParent() != null) {
            Files.createDirectories(inputPath.getParent());
        }
        Files.write(inputPath, sampleInputLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // Run the existing in-memory batch runner (parses the lines and executes)
        List<String> outputs = runManualNetworkBatchFromRawLines(sampleInputLines);

        // Write outputs to output file (one line per output)
        Path outputPath = Paths.get(OUTPUT);
        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }
        Files.write(outputPath, outputs, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // Print a short summary so manual runs are easier to debug
        System.out.println("[ManualTestingFramework] Wrote " + sampleInputLines.size() + " inputs to " + inputPath);
        System.out.println("[ManualTestingFramework] Wrote " + outputs.size() + " outputs to " + outputPath);
    }
}

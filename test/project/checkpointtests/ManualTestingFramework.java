package project.checkpointtests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class ManualTestingFramework {

    // Constants expected by Checkpoint4TestSuite
    public static final String INPUT = "input.data";
    public static final String OUTPUT = "output.data";

    // Parses raw string lines into integers, skipping blank and malformed entries.
    public static List<Integer> parseInputLines(List<String> rawLines) {
        List<Integer> ints = new ArrayList<>();
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
        Path inputPath = Paths.get(INPUT);
        List<String> rawLines = new ArrayList<>();

        // If the input file exists, read it; otherwise use an empty list
        if (Files.exists(inputPath)) {
            rawLines = Files.readAllLines(inputPath);
        }

        // Run the batch using the raw lines from the input file (this produces a List<String> results)
        List<String> outputs = runManualNetworkBatchFromRawLines(rawLines);

        // Join outputs with commas into a single line (if no outputs, write an empty line)
        String singleLine = "";
        if (outputs != null && !outputs.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < outputs.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(outputs.get(i));
            }
            singleLine = sb.toString();
        }

        Path outputPath = Paths.get(OUTPUT);
        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }
        // Write the single comma-separated line (truncate existing file)
        Files.write(outputPath, singleLine.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // Small console summary for manual runs
        System.out.println("[ManualTestingFramework] Read " + rawLines.size() + " input lines from " + inputPath);
        System.out.println("[ManualTestingFramework] Wrote " + (singleLine.isEmpty() ? 0 : outputs.size()) + " outputs to " + outputPath);
    }
}

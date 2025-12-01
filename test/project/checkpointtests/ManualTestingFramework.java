package project.checkpointtests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import apiimplementation.ConceptualAPIImpl;
import apiimplementation.NetworkAPIImpl;
import apinetwork.Delimiters;
import apinetwork.JobRequest;
import apistorage.ProcessAPIFileImpl;

public class ManualTestingFramework {

    // Constants used by tests
    public static final String INPUT = "input.data";
    public static final String OUTPUT = "output.data";


    // Parse raw string lines into integers (skip blanks/malformed).
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

  
    public static void main(String[] args) throws IOException {
        Path inputPath = Paths.get(INPUT);
        Path outputPath = Paths.get(OUTPUT);

        // Ensure input file exists (the checkpoint tests will write this file before calling main)
        if (!Files.exists(inputPath)) {
            // nothing to do — create an empty input file to avoid downstream error
            Files.createFile(inputPath);
        }

        // Ensure parent directories exist for output if needed
        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }
        if (!Files.exists(outputPath)) {
            Files.createFile(outputPath);
        }

        // Use the real file-backed ProcessAPI implementation
        ProcessAPIFileImpl fileStore = new ProcessAPIFileImpl(inputPath.toString(), outputPath.toString());

        // Wire real conceptual engine and network coordinator
        ConceptualAPIImpl conceptual = new ConceptualAPIImpl();
        NetworkAPIImpl network = new NetworkAPIImpl(fileStore, conceptual);

        // Call the network API in batch mode (sentinel -1)
        JobRequest batchJob = new JobRequest(-1, new Delimiters(":", " × "));
        network.sendJob(batchJob);

        // Post-process outputs file into single CSV line for checkpoint-4 test compatibility ---
        // Read whatever the ProcessAPIFileImpl wrote (one result per line)
        List<String> writtenLines = new ArrayList<>();
        if (Files.exists(outputPath)) {
            writtenLines = Files.readAllLines(outputPath);
        }

        // Filter out any markers we don't want in the final CSV (optional)
        // For checkpoint compatibility we include only non-empty lines that are not batch markers.
        List<String> resultLines = new ArrayList<>();
        for (String line : writtenLines) {
            if (line == null) {
                continue;
            }
            String t = line.trim();
            if (t.isEmpty()) {
                continue;
            }
            // Skip internal markers like "batch:completed:..." if desired; keep factorization results.
            if (t.startsWith("batch:") && !t.startsWith("batch:completed:")) {
                continue;
            }
            // If the marker is a summary "batch:completed:N", include it as the last field if you want;
            // but checkpoint 4 expected exactly one output per input, so here we only keep factorization lines.
            if (t.startsWith("batch:completed:")) {
                continue;
            }
            resultLines.add(t);
        }

        // Join into a single comma-separated line
        String singleLine = "";
        if (!resultLines.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < resultLines.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(resultLines.get(i));
            }
            singleLine = sb.toString();
        }

        // Overwrite the output file with the single CSV line (truncate existing)
        Files.write(outputPath, singleLine.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // console output for manual runs
        System.out.println("[ManualTestingFramework] Read " + Files.readAllLines(inputPath).size() + " input lines from " + inputPath);
        System.out.println("[ManualTestingFramework] Wrote " + (singleLine.isEmpty() ? 0 : resultLines.size()) + " CSV outputs to " + outputPath);
    }
}

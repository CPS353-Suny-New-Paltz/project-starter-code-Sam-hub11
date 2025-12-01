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
import apinetwork.JobRequest;
import apinetwork.Delimiters;
import apistorage.ProcessAPIFileImpl;

public class ManualTestingFramework {

    public static final String INPUT = "input.data";
    public static final String OUTPUT = "output.data";

    public static void main(String[] args) throws IOException {
        Path inputPath = Paths.get(INPUT);
        Path outputPath = Paths.get(OUTPUT);

        // Ensure input file exists (the test writes it before calling main)
        if (!Files.exists(inputPath)) {
            Files.createFile(inputPath);
        }

        // Ensure output path parent exists
        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }
        if (!Files.exists(outputPath)) {
            Files.createFile(outputPath);
        }

        // Use file-backed store so NetworkAPIImpl writes one line per result
        ProcessAPIFileImpl fileStore = new ProcessAPIFileImpl(inputPath.toString(), outputPath.toString());

        ConceptualAPIImpl conceptual = new ConceptualAPIImpl();
        NetworkAPIImpl network = new NetworkAPIImpl(fileStore, conceptual);

        // Run batch job (network writes each factorization/result as its own line,
        // and may also write markers like "batch:completed:3" or "batch success")
        JobRequest batchJob = new JobRequest(-1, new Delimiters(":", " × "));
        network.sendJob(batchJob);

        // Read whatever the network wrote to the output file
        List<String> writtenLines = new ArrayList<>();
        if (Files.exists(outputPath)) {
            writtenLines = Files.readAllLines(outputPath);
        }

        // Keep only "data" lines (skip markers), trim each entry, and join with NO spaces
        List<String> resultLines = new ArrayList<>();
        for (String line : writtenLines) {
            if (line == null) {
                continue;
            }
            String t = line.trim();
            if (t.isEmpty()) {
                continue;
            }
            String lower = t.toLowerCase();
            // skip any variant of markers containing 'batch', 'network', or 'error'
            if (lower.contains("batch") || lower.contains("network") || lower.contains("error")) {
                continue;
            }
            // Accept this as a result line (trimmed)
            resultLines.add(t);
        }

        // Join into a single comma-separated line (no spaces after commas)
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

        // Overwrite the output file with the single CSV line
        Files.write(outputPath, singleLine.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // Helpful log for manual runs (not used by tests)
        System.out.println("[ManualTestingFramework] Wrote CSV: " + singleLine);
    }
}

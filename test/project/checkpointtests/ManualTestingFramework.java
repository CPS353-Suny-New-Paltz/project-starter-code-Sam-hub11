package project.checkpointtests;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

        // Ensure input file exists
        if (!Files.exists(inputPath)) {
            Files.createFile(inputPath);
        }

        // Ensure output path parent exists and file exists
        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }
        if (!Files.exists(outputPath)) {
            Files.createFile(outputPath);
        }

        // Clear output file at the start to prevent duplicates from previous runs
        Files.writeString(outputPath, "", StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);

        // Use file-backed store so NetworkAPIImpl writes one line per result
        ProcessAPIFileImpl fileStore = new ProcessAPIFileImpl(inputPath.toString(), outputPath.toString());

        ConceptualAPIImpl conceptual = new ConceptualAPIImpl();
        NetworkAPIImpl network = new NetworkAPIImpl(fileStore, conceptual);

        // Run batch job
        JobRequest batchJob = new JobRequest(-1, new Delimiters(":", " × "));
        network.sendJob(batchJob);

        // Read all lines
        List<String> writtenLines = new ArrayList<>();
        if (Files.exists(outputPath)) {
            writtenLines = Files.readAllLines(outputPath, StandardCharsets.UTF_8);
        }

        System.out.println("[ManualTestingFramework] RAW LINES READ: " + writtenLines);

        // Filter out markers and take first token of factorization
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
            if (lower.contains("batch") || lower.contains("network") || lower.contains("error")) {
                continue; // skip marker lines
            }
            // Take only first token before any space or '×'
            String firstToken = t.split("[ ×]")[0].trim();
            resultLines.add(firstToken);
        }

        // Join into single comma-separated line
        String singleLine = String.join(",", resultLines);

        // Overwrite the output file
        Files.writeString(outputPath, singleLine, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("[ManualTestingFramework] Wrote CSV: " + singleLine);
    }
}

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

        // ensure parent directories for output exist
        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }
        if (!Files.exists(outputPath)) {
            Files.createFile(outputPath);
        }

        // Use file-backed ProcessAPI to allow NetworkAPIImpl to write per-line
        ProcessAPIFileImpl fileStore = new ProcessAPIFileImpl(inputPath.toString(), outputPath.toString());

        ConceptualAPIImpl conceptual = new ConceptualAPIImpl();
        NetworkAPIImpl network = new NetworkAPIImpl(fileStore, conceptual);

        // Run batch job (Network writes one-line-per-result then a batch:completed:N marker)
        JobRequest batchJob = new JobRequest(-1, new Delimiters(":", " × "));
        network.sendJob(batchJob);

        // Now read everything the network wrote and collapse into a single comma-separated line
        List<String> writtenLines = new ArrayList<>();
        if (Files.exists(outputPath)) {
            writtenLines = Files.readAllLines(outputPath);
        }

        // Keep only factorization lines (exclude batch markers and empty lines)
        List<String> resultLines = new ArrayList<>();
        for (String line : writtenLines) {
            if (line == null) {
                continue;
            }
            String t = line.trim();
            if (t.isEmpty()) {
                continue;
            }
            if (t.startsWith("batch:")) {
                // skip summary or other batch markers
                continue;
            }
            if (t.startsWith("network:") || t.startsWith("error:")) {
                // These are error markers — you may want to include or exclude them depending on your policy.
                // For checkpoint compatibility, skip them from the single CSV output.
                continue;
            }
            resultLines.add(t);
        }

        // Join into one comma-separated line and overwrite file
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

        Files.write(outputPath, singleLine.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("[ManualTestingFramework] Wrote CSV: " + singleLine);
    }
}

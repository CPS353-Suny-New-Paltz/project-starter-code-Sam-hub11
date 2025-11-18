package apistorage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Reads integers from inputPath (one per line) and appends outputs to outputPath.
public class ProcessAPIFileImpl implements ProcessAPI {
    private final File inputFile;
    private final File outputFile;

    public ProcessAPIFileImpl(String inputPath, String outputPath) {
        this.inputFile = new File(inputPath);
        this.outputFile = new File(outputPath);
    }

    @Override
    public List<Integer> readInputs() {
        List<Integer> ints = new ArrayList<>();
        if (!inputFile.exists()) {
            System.err.println("[ProcessAPIFileImpl] Input file not found: " + inputFile.getPath());
            return ints;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String t = line.trim();
                if (t.isEmpty()) continue;
                try {
                    ints.add(Integer.parseInt(t));
                } catch (NumberFormatException nfe) {
                    System.err.println("[ProcessAPIFileImpl] Skipping non-int line: " + t);
                }
            }
        } catch (IOException ioe) {
            System.err.println("[ProcessAPIFileImpl] Error reading input file: " + ioe.getMessage());
        }
        return ints;
    }

    @Override
    public boolean writeOutput(String data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile, true))) {
            bw.write(data);
            bw.newLine();
            return true;
        } catch (IOException ioe) {
            System.err.println("[ProcessAPIFileImpl] Error writing output file: " + ioe.getMessage());
            return false;
        }
    }
}

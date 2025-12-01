package testhelpers;

import apistorage.ProcessAPI;

import java.util.ArrayList;
import java.util.List;

public class TestProcessDataStore implements ProcessAPI {

    private final TestInputConfig inputConfig;
    private final TestOutputConfig outputConfig;

    public TestProcessDataStore(TestInputConfig inputConfig, TestOutputConfig outputConfig) {
        this.inputConfig = inputConfig;
        this.outputConfig = outputConfig;
    }

    @Override
    public List<Integer> readInputs() {
    	List<Integer> src = inputConfig == null ? null : inputConfig.getInputs();
    	if (src == null) {
    	    return new ArrayList<>();
    	}
    	return new ArrayList<>(src);
    }

 //will write a string output to the TestOutputConfig
 
    @Override
    public boolean writeOutput(String data) {
        try {
            if (outputConfig == null) {
                System.out.println("[TestProcessDataStore] writeOutput -> outputConfig is null, data=" + data);
                return false;
            }

            // Normalize the string to avoid whitespace mismatches
            String normalized = data == null ? null : data.trim();

            // If null, treat as failed write
            if (normalized == null) {
                System.out.println("[TestProcessDataStore] writeOutput -> null data, ignoring");
                return false;
            }

            // If this line looks like a marker (batch/network/error) do NOT store it.
            String lower = normalized.toLowerCase();
            if (lower.contains("batch") || lower.contains("network") || lower.contains("error")) {
                // Debug print to show we received a marker but we will not store it.
                System.out.println("[TestProcessDataStore] marker received (not stored): \"" + normalized + "\"");
                // Return true so the caller sees a successful write.
                return true;
            }

            // This is an actual result line — store it and print what was stored.
            outputConfig.write(normalized);
            System.out.println("[TestProcessDataStore] STORED: \"" + normalized + "\"");
            return true;
        } catch (Throwable t) {
            System.out.println("[TestProcessDataStore] writeOutput THREW: " + t.getMessage());
            return false;
        }
    }






    //Test helper to access the TestOutputConfig for assertions in tests.

    public TestOutputConfig getOutputConfig() {
        return outputConfig;
    }
}

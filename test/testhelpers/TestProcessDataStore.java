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
            // Debug logging so we can see exactly what tests observe
            System.out.println("[TestProcessDataStore] writeOutput: \"" + data + "\"");
            outputConfig.write(data);
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

package apiimplementation;

import apistorage.ProcessAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProcessAPIImpl implements ProcessAPI {
    private final List<Integer> inputs = new ArrayList<>();
    private final List<String> outputs = new ArrayList<>();

    public ProcessAPIImpl() {

    }


    @Override
    public List<Integer> readInputs() {
        return Collections.unmodifiableList(new ArrayList<>(inputs));
    }

    @Override
    public boolean writeOutput(String data) {
        try {
            // debug log so CI/test output shows what's written
            System.out.println("[ProcessAPIImpl] writeOutput: " + data);
            outputs.add(data);
            return true;
        } catch (Exception e) {
            System.out.println("[ProcessAPIImpl] writeOutput THREW: " + e.getMessage());
            return false;
        }
    }


    // helpers for tests
    public List<String> getAllOutputs() {
        return Collections.unmodifiableList(new ArrayList<>(outputs));
    }

    public void addInput(int v) {
        inputs.add(v);
    }
}

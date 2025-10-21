package apiimplementation;

import java.util.ArrayList;
import java.util.List;

import apistorage.ProcessAPI;

public class ProcessAPIImpl implements ProcessAPI {
    private final List<String> outputs = new ArrayList<>();
    private final List<String> inputs = new ArrayList<>();

    public ProcessAPIImpl() { }

    @Override
    public List<String> readInput() {
        return inputs;
    }

    @Override
    public boolean writeOutput(String data) {
        try {
            outputs.add(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // helpers for tests
    public List<String> getOutputs() { return outputs; }
    public void addInput(String s) { inputs.add(s); }
}

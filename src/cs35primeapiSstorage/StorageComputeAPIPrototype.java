package cs35primeapiSstorage;

import cs353apinetwork.ComputationInput;
import cs353apinetwork.ComputationOutput;
import project.annotations.ProcessAPIPrototype;

public class StorageComputeAPIPrototype implements StorageComputeAPI {

    @ProcessAPIPrototype
    @Override
    public ComputationInput readInput() {
        return new ComputationInput(new int[]{60}); // prototype input
    }

    @Override
    public void writeOutput(ComputationOutput output) {
        System.out.println("Prototype output: " + output);
    }
}

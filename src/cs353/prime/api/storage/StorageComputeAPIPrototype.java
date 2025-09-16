package cs353.prime.api.storage;

import cs353.api.network.*;
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

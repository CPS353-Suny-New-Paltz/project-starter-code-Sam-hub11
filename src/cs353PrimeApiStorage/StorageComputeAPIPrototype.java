package cs353PrimeApiStorage;

import cs353ApiNetwork.ComputationInput;
import cs353ApiNetwork.ComputationOutput;
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

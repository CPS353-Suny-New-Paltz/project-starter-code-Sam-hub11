package cs353PrimeApiStorage;


import cs353ApiNetwork.ComputationInput;
import cs353ApiNetwork.ComputationOutput;
import project.annotations.ProcessAPI;

@ProcessAPI
public interface StorageComputeAPI {
    ComputationInput readInput();
    void writeOutput(ComputationOutput output);
}

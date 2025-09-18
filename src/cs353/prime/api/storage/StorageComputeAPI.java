package cs353.prime.api.storage;


import cs353.api.network.ComputationInput;
import cs353.api.network.ComputationOutput;
import project.annotations.ProcessAPI;

@ProcessAPI
public interface StorageComputeAPI {
    ComputationInput readInput();
    void writeOutput(ComputationOutput output);
}

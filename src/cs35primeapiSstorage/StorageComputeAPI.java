package cs35primeapiSstorage;


import cs353apinetwork.ComputationInput;
import cs353apinetwork.ComputationOutput;
import project.annotations.ProcessAPI;

@ProcessAPI
public interface StorageComputeAPI {
    ComputationInput readInput();
    void writeOutput(ComputationOutput output);
}

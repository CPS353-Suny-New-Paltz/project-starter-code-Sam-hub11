package apistorage;


import apinetwork.ComputationInput;
import apinetwork.ComputationOutput;
import project.annotations.ProcessAPI;

@ProcessAPI
public interface StorageComputeAPI {
    ComputationInput readInput();
    void writeOutput(ComputationOutput output);
}

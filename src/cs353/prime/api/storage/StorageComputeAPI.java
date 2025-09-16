package cs353.prime.api.storage;

import cs353.api.network.*;
import project.annotations.NetworkAPIPrototype;
import project.annotations.ProcessAPI;

@ProcessAPI
public interface StorageComputeAPI {
    ComputationInput readInput();
    void writeOutput(ComputationOutput output);
}

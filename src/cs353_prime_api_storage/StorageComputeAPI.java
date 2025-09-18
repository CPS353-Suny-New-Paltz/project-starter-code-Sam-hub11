package cs353_prime_api_storage;

import cs353_api_network.*;
import project.annotations.ProcessAPI;

@ProcessAPI
public interface StorageComputeAPI {
    ComputationInput readInput();
    void writeOutput(ComputationOutput output);
}

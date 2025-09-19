package apistorage;

import project.annotations.ProcessAPI;

@ProcessAPI
public interface StorageComputeAPI {
    int readInput();
    void writeOutput(String output);
}

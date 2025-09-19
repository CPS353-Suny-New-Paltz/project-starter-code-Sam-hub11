package apistorage;

import project.annotations.ProcessAPIPrototype;

public class StorageComputeAPIPrototype {
    @ProcessAPIPrototype
    public StorageComputeAPI prototype(StorageComputeAPI api) {
        return new StorageComputeAPI() {
            private int storedInput;

            @Override
            public int readInput() {
                storedInput = 123456; // example input
                return storedInput;
            }

            @Override
            public void writeOutput(String output) {
                System.out.println("Computation result: " + output);
            }
        };
    }
}

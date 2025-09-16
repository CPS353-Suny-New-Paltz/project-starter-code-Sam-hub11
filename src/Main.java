import cs353.prime.api.storage.*;
import cs353.api.network.*;
import cs353.prime.api.engine.*;

public class Main {
    public static void main(String[] args) {
        StorageComputeAPI storage = new StorageComputeAPIPrototype();
        ComputeEngineAPI engine = new ComputeEngineAPIPrototype();

        ComputationInput input = storage.readInput();
        ComputationOutput output = engine.runComputation(input);

        storage.writeOutput(output);
    }
}

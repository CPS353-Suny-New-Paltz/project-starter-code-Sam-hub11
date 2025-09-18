package cs353_prime_api_engine;

import cs353_api_network.ComputationInput;
import cs353_api_network.ComputationOutput;
import project.annotations.ConceptualAPIPrototype;

public class ComputeEngineAPIPrototype implements ComputeEngineAPI {

    @ConceptualAPIPrototype
    @Override
    public ComputationOutput runComputation(ComputationInput input) {
        // prototype only – return dummy factorization
        return new ComputationOutput("2 × 2 × 3 × 5");
    }
}

package cs353.prime.api.engine;

import cs353.api.network.ComputationInput;
import cs353.api.network.ComputationOutput;
import project.annotations.ConceptualAPIPrototype;

public class ComputeEngineAPIPrototype implements ComputeEngineAPI {

    @ConceptualAPIPrototype
    @Override
    public ComputationOutput runComputation(ComputationInput input) {
        // prototype only – return dummy factorization
        return new ComputationOutput("2 × 2 × 3 × 5");
    }
}

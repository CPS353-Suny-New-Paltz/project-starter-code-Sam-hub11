package cs353PrimeApiEngine;

import cs353ApiNetwork.ComputationInput;
import cs353ApiNetwork.ComputationOutput;
import project.annotations.ConceptualAPIPrototype;

public class ComputeEngineAPIPrototype implements ComputeEngineAPI {

    @ConceptualAPIPrototype
    @Override
    public ComputationOutput runComputation(ComputationInput input) {
        // prototype only – return dummy factorization
        return new ComputationOutput("2 × 2 × 3 × 5");
    }
}

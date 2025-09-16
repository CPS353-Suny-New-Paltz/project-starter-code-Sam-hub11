package cs353.prime.api.engine;
import cs353.api.network.*;
import project.annotations.ConceptualAPIPrototype;

public class ComputeEngineAPIPrototype implements ComputeEngineAPI {

    @ConceptualAPIPrototype
    @Override
    public ComputationOutput runComputation(ComputationInput input) {
        // prototype only – return dummy factorization
        return new ComputationOutput("2 × 2 × 3 × 5");
    }
}

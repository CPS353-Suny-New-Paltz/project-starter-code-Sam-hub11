package cs353.prime.api.engine;

import cs353.api.network.ComputationInput;
import cs353.api.network.ComputationOutput;
import project.annotations.ConceptualAPI;

@ConceptualAPI
public interface ComputeEngineAPI {

    ComputationOutput runComputation(ComputationInput input);
}

package cs353.prime.api.engine;

import cs353.api.network.*;
import project.annotations.ConceptualAPI;

@ConceptualAPI
public interface ComputeEngineAPI {

    ComputationOutput runComputation(ComputationInput input);
}

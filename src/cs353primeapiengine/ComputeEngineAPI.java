package cs353primeapiengine;

import cs353apinetwork.ComputationInput;
import cs353apinetwork.ComputationOutput;
import project.annotations.ConceptualAPI;

@ConceptualAPI
public interface ComputeEngineAPI {

    ComputationOutput runComputation(ComputationInput input);
}

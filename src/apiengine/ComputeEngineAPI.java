package apiengine;

import apinetwork.ComputationInput;
import apinetwork.ComputationOutput;
import project.annotations.ConceptualAPI;

@ConceptualAPI
public interface ComputeEngineAPI {

    ComputationOutput runComputation(ComputationInput input);
}

package cs353PrimeApiEngine;

import cs353ApiNetwork.ComputationInput;
import cs353ApiNetwork.ComputationOutput;
import project.annotations.ConceptualAPI;

@ConceptualAPI
public interface ComputeEngineAPI {

    ComputationOutput runComputation(ComputationInput input);
}

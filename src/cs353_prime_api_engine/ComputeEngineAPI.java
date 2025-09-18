package cs353_prime_api_engine;

import cs353_api_network.ComputationInput;
import cs353_api_network.ComputationOutput;
import project.annotations.ConceptualAPI;

@ConceptualAPI
public interface ComputeEngineAPI {

    ComputationOutput runComputation(ComputationInput input);
}

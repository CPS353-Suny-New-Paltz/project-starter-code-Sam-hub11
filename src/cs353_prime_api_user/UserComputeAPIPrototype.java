package cs353_prime_api_user;

import cs353_api_network.Delimiters;
import cs353_api_network.Input;
import cs353_api_network.JobRequest;
import cs353_api_network.Output;
import project.annotations.NetworkAPIPrototype;

public class UserComputeAPIPrototype  implements UserComputeAPI {

    @NetworkAPIPrototype
    @Override
    public JobRequest createJob(Input source, Output dest, Delimiters delimiters) {
        if (delimiters == null) {
            delimiters = new Delimiters(";", ":");
        }
        return new JobRequest(source, dest, delimiters);
    }
}

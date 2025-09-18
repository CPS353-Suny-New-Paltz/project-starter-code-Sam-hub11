package cs353.prime.api.user;

import cs353.api.network.Delimiters;
import cs353.api.network.Input;
import cs353.api.network.JobRequest;
import cs353.api.network.Output;
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

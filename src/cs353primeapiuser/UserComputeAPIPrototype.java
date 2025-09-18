package cs353primeapiuser;

import cs353apinetwork.Delimiters;
import cs353apinetwork.Input;
import cs353apinetwork.JobRequest;
import cs353apinetwork.Output;
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

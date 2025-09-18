package cs353PrimeApiUser;

import cs353ApiNetwork.Delimiters;
import cs353ApiNetwork.Input;
import cs353ApiNetwork.JobRequest;
import cs353ApiNetwork.Output;
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

package cs353.prime.api.user;

import cs353.api.network.Delimiters;
import cs353.api.network.Input;
import cs353.api.network.JobRequest;
import cs353.api.network.Output;
import project.annotations.NetworkAPI;

@NetworkAPI
public interface UserComputeAPI 
{
    JobRequest createJob(Input source, Output dest, Delimiters delimiters);
}

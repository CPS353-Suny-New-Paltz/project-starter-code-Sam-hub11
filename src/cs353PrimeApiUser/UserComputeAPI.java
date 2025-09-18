package cs353PrimeApiUser;

import cs353ApiNetwork.Delimiters;
import cs353ApiNetwork.Input;
import cs353ApiNetwork.JobRequest;
import cs353ApiNetwork.Output;
import project.annotations.NetworkAPI;

@NetworkAPI
public interface UserComputeAPI 
{
    JobRequest createJob(Input source, Output dest, Delimiters delimiters);
}

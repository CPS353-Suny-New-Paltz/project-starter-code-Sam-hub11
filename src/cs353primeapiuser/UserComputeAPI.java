package cs353primeapiuser;

import cs353apinetwork.Delimiters;
import cs353apinetwork.Input;
import cs353apinetwork.JobRequest;
import cs353apinetwork.Output;
import project.annotations.NetworkAPI;

@NetworkAPI
public interface UserComputeAPI 
{
    JobRequest createJob(Input source, Output dest, Delimiters delimiters);
}

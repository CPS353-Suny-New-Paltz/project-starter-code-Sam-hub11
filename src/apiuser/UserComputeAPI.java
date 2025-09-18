package apiuser;

import apinetwork.Delimiters;
import apinetwork.Input;
import apinetwork.JobRequest;
import apinetwork.Output;
import project.annotations.NetworkAPI;

@NetworkAPI
public interface UserComputeAPI {
    JobRequest createJob(Input source, Output dest, Delimiters delimiters);
}

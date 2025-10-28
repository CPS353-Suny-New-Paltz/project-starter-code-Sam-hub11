package apiengine;

import apinetwork.JobRequest;
import apinetwork.Delimiters;
import apinetwork.ComputationOutput;

public class NetworkAPIPrototype {

    public void runPrototype(NetworkAPI api) {
        // Create a simple job request
        JobRequest job = new JobRequest(10, new Delimiters(":", " × "));

        // Call the API (which should eventually process the job)
        ComputationOutput result = api.sendJob(job);

        // Print placeholder output for prototype verification
        System.out.println("[NetworkAPIPrototype] Sent job: " + job.getInputNumber());
        System.out.println("[NetworkAPIPrototype] Received output: " + result.getResult());
    }
}

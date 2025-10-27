package apiengine;

import apiimplementation.NetworkAPIImpl;
import apinetwork.JobRequest;
import apinetwork.ComputationOutput;
import apistorage.ProcessAPI;
import apinetwork.Delimiters;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestNetworkAPI {

    @Test
    public void testSendJob_ValidRequest() {
        ProcessAPI mockProcess = Mockito.mock(ProcessAPI.class);
        when(mockProcess.writeOutput(anyString())).thenReturn(true);

        NetworkAPIImpl network = new NetworkAPIImpl(mockProcess);
        JobRequest req = new JobRequest(5, new Delimiters(":", " × "));

        ComputationOutput out = network.sendJob(req);

        assertNotNull(out, "ComputationOutput should not be null");
        verify(mockProcess).writeOutput(anyString());
    }

    @Test
    public void testSendJob_NullRequest() {
        ProcessAPI mockProcess = Mockito.mock(ProcessAPI.class);
        NetworkAPIImpl network = new NetworkAPIImpl(mockProcess);

        ComputationOutput out = network.sendJob(null);

        assertNotNull(out, "ComputationOutput should be returned even for null request");
        verify(mockProcess).writeOutput(anyString());
    }
}

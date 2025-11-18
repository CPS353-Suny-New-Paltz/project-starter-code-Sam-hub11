package apiengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import apiimplementation.ConceptualAPIImpl;
import apiimplementation.NetworkAPIImpl;
import apinetwork.ComputationOutput;
import apinetwork.Delimiters;
import apinetwork.JobRequest;
import testhelpers.TestInputConfig;
import testhelpers.TestOutputConfig;
import testhelpers.TestProcessDataStore;

import java.util.Collections;
import java.util.List;

public class TestNetworkAPI {

    @Test
    public void testSendJob_ValidRequest_writesResultToStore() {
        // Arrange: empty store (we'll just test a single-job write)
        TestInputConfig in = new TestInputConfig(Collections.emptyList());
        TestOutputConfig outConfig = new TestOutputConfig();
        TestProcessDataStore store = new TestProcessDataStore(in, outConfig);

        // Coordinator uses the test store and pure conceptual engine
        ConceptualAPIImpl conceptual = new ConceptualAPIImpl();
        NetworkAPIImpl network = new NetworkAPIImpl(store, conceptual);

        // Act: single job
        JobRequest req = new JobRequest(5, new Delimiters(":", " × "));
        ComputationOutput out = network.sendJob(req);

        // Assert: returned output non-null and written to store
        assertNotNull(out, "ComputationOutput should not be null");
        List<String> outputs = outConfig.getOutputs();
        assertEquals(1, outputs.size(), "Single job should produce one output");
        assertEquals("5", outputs.get(0), "Single job for input 5 should write '5'");
    }

    @Test
    public void testSendJob_NullRequest_writesMarkerAndReturnsInvalid() {
        // Arrange
        TestInputConfig in = new TestInputConfig(Collections.emptyList());
        TestOutputConfig outConfig = new TestOutputConfig();
        TestProcessDataStore store = new TestProcessDataStore(in, outConfig);

        ConceptualAPIImpl conceptual = new ConceptualAPIImpl();
        NetworkAPIImpl network = new NetworkAPIImpl(store, conceptual);

        // Act: null job
        ComputationOutput out = network.sendJob(null);

        // Assert: non-null result and marker written to store
        assertNotNull(out, "ComputationOutput should not be returned even for null request");
        List<String> outputs = outConfig.getOutputs();
        assertEquals(1, outputs.size(), "Null job should result in one output marker");
        assertEquals("network:null-job", outputs.get(0), "Expected null-job marker written");
    }
}

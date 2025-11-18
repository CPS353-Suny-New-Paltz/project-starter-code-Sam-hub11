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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Tests for NetworkAPIImpl that assert observable outputs (no Mockito.verify).
 */
public class TestNetworkAPI {

    @Test
    public void testSendJob_ValidRequest_writesResultToStore() {
        TestInputConfig in = new TestInputConfig(Collections.emptyList());
        TestOutputConfig outConfig = new TestOutputConfig();
        TestProcessDataStore store = new TestProcessDataStore(in, outConfig);

        ConceptualAPIImpl conceptual = new ConceptualAPIImpl();
        NetworkAPIImpl network = new NetworkAPIImpl(store, conceptual);

        JobRequest req = new JobRequest(5, new Delimiters(":", " × "));
        ComputationOutput out = network.sendJob(req);

        assertNotNull(out, "ComputationOutput should not be null");
        List<String> outputs = outConfig.getOutputs();
        assertEquals(1, outputs.size(), "Single job should produce one output");
        assertEquals("5", outputs.get(0), "Single job for input 5 should write '5'");
    }

    @Test
    public void testSendJob_BatchMode_readsStoreAndWritesResults() {
        TestInputConfig in = new TestInputConfig(Arrays.asList(1, 10, 25));
        TestOutputConfig outConfig = new TestOutputConfig();
        TestProcessDataStore store = new TestProcessDataStore(in, outConfig);

        ConceptualAPIImpl conceptual = new ConceptualAPIImpl();
        NetworkAPIImpl network = new NetworkAPIImpl(store, conceptual);

        JobRequest batchJob = new JobRequest(-1, new Delimiters(":", " × "));
        ComputationOutput summary = network.sendJob(batchJob);

        assertNotNull(summary);
        List<String> outputs = outConfig.getOutputs();
        assertEquals(3, outputs.size()); // 3 results
        assertEquals("1", outputs.get(0));
        assertEquals("2 × 5", outputs.get(1));
        assertEquals("5 × 5", outputs.get(2));
    }
}

package integration;

import apiimplementation.ConceptualAPIImpl;
import apinetwork.ComputationInput;
import apinetwork.Delimiters;
import org.junit.jupiter.api.Test;
import testhelpers.TestInputConfig;
import testhelpers.TestOutputConfig;
import testhelpers.TestProcessDataStore;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ComputeEngineIntegrationTest {

    @Test
    public void integration_initialInputs_writeExpectedOutputs() {
        // Arrange - use test helpers
        TestInputConfig in = new TestInputConfig(Arrays.asList(1, 10, 25));
        TestOutputConfig out = new TestOutputConfig();
        TestProcessDataStore store = new TestProcessDataStore(in, out);

        ConceptualAPIImpl engine = new ConceptualAPIImpl(store);

        // Act - compute for each input
        for (Integer n : store.readInputs()) {
            // use default delimiters (pair delimiter " × ")
            engine.compute(new ComputationInput(n, new Delimiters(":", " × ")));
        }

        // Assert
        List<String> expected = Arrays.asList("1", "2 × 5", "5 × 5");
        assertEquals(expected.size(), out.getOutputs().size(), "expected one output per input");
        assertEquals(expected, out.getOutputs(), "outputs should match expected prime factorizations");
    }
}

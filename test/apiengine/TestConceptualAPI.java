package apiengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import apiimplementation.ConceptualAPIImpl;
import apinetwork.ComputationInput;
import apinetwork.ComputationOutput;
import apinetwork.Delimiters;

public class TestConceptualAPI {

    @Test
    public void compute_shouldReturnCorrectFactorization_and_notPerformIo() {
        // Arrange: pure conceptual engine (no ProcessAPI injected)
        ConceptualAPIImpl engine = new ConceptualAPIImpl();

        // Act: compute factorization for 10
        ComputationInput input = new ComputationInput(10, new Delimiters(":", " × "));
        ComputationOutput out = engine.compute(input);

        // Assert: returned output is correct and non-null
        assertNotNull(out, "ComputationOutput should not be null");
        assertNotNull(out.getResult(), "ComputationOutput result should not be null");
        assertEquals("2 × 5", out.getResult(), "10 should factor to '2 × 5'");
    }

    @Test
    public void compute_nullInput_returnsNullString_and_noIo() {
        ConceptualAPIImpl engine = new ConceptualAPIImpl();

        ComputationOutput out = engine.compute(null);

        assertNotNull(out, "ComputationOutput should not be null even for null input");
        assertEquals("null", out.getResult(), "Null input should return 'null'");
    }
}

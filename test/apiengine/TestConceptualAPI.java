package apiengine;

import apiimplementation.ConceptualAPIImpl;
import apinetwork.ComputationInput;
import apinetwork.Delimiters;
import apistorage.ProcessAPI;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestConceptualAPI {
    @Test
    public void smokeTest_compute_invokesProcessWrite() {
        ProcessAPI mockProcess = Mockito.mock(ProcessAPI.class);
        when(mockProcess.writeOutput(anyString())).thenReturn(true);

        ConceptualAPIImpl engine = new ConceptualAPIImpl(mockProcess);
        ComputationInput input = new ComputationInput(10, new Delimiters(":", ","));

        assertNotNull(engine.compute(input));
        verify(mockProcess, atLeastOnce()).writeOutput(anyString());
    }
}

package apistorage;

import apiimplementation.ProcessAPIImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestProcessAPI {

    @Test
    public void readInputs_defaultEmpty_thenAddInput_reflectsInRead() {
        ProcessAPIImpl store = new ProcessAPIImpl();

        // initially no inputs
        List<Integer> initial = store.readInputs();
        assertNotNull(initial);
        assertEquals(0, initial.size(), "new store should have zero inputs by default");

        // add a test input — ProcessAPIImpl exposes addInput(int) helper from earlier design
        store.addInput(42);
        List<Integer> after = store.readInputs();
        assertEquals(1, after.size(), "store should reflect added input");
        assertEquals(42, after.get(0).intValue());
    }

    @Test
    public void writeOutput_returnsTrue_andOutputsStored() {
        ProcessAPIImpl store = new ProcessAPIImpl();
        boolean ok = store.writeOutput("hello");
        assertTrue(ok, "writeOutput should return true for successful write");

        // helper to inspect outputs
        assertTrue(store.getAllOutputs().contains("hello"));
    }
}

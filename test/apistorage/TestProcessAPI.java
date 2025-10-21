package apistorage;

import org.junit.jupiter.api.Test;

import apiimplementation.ProcessAPIImpl;

import static org.junit.jupiter.api.Assertions.*;

public class TestProcessAPI {
    @Test
    public void smokeTest_writeOutput_noExceptions() {
        ProcessAPIImpl impl = new ProcessAPIImpl();
        impl.writeOutput("sample");
        assertTrue(true);
    }
}

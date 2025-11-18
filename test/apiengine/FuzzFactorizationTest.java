package apiengine;

import apiimplementation.ConceptualAPIImpl;
import apinetwork.ComputationInput;
import org.junit.jupiter.api.Test;
import java.util.Random;

public class FuzzFactorizationTest {
    @Test
    public void fuzz_test_printSeed() {
        long seed = System.currentTimeMillis();
        System.out.println("Fuzz seed: " + seed);
        Random rnd = new Random(seed);

        // we don't need the test store here — this fuzz test exercises pure computation
        ConceptualAPIImpl engine = new ConceptualAPIImpl();

        for (int i = 0; i < 500; i++) {
            int v = 1 + rnd.nextInt(100000);
            engine.compute(new ComputationInput(v, ":"));
        }
    }
}

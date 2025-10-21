package apiimplementation;

import apiengine.ConceptualAPI;
import apinetwork.ComputationInput;
import apinetwork.ComputationOutput;
import apistorage.ProcessAPI;

import java.util.ArrayList;
import java.util.List;

public class ConceptualAPIImpl implements ConceptualAPI {
    private final ProcessAPI processAPI;

    public ConceptualAPIImpl(ProcessAPI processAPI) {
        this.processAPI = processAPI;
    }

    @Override
    public ComputationOutput compute(ComputationInput input) {
        testingHook(); // test hook - no-op in production

        int n = (input == null) ? 0 : input.getInputNumber();
        if (n <= 1) {
            String s = String.valueOf(n);
            processAPI.writeOutput(s);
            return new ComputationOutput(s);
        }
        List<Integer> factors = primeFactors(n);
        String result = joinFactors(factors);
        processAPI.writeOutput(result);
        return new ComputationOutput(result);
    }

    private List<Integer> primeFactors(int n) {
        List<Integer> factors = new ArrayList<>();
        while (n % 2 == 0) { factors.add(2); n /= 2; }
        int factor = 3;
        while (factor * factor <= n) {
            while (n % factor == 0) { factors.add(factor); n /= factor; }
            factor += 2;
        }
        if (n > 1) factors.add(n);
        return factors;
    }

    private String joinFactors(List<Integer> factors) {
        if (factors == null || factors.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < factors.size(); i++) {
            if (i > 0) sb.append(" × ");
            sb.append(factors.get(i));
        }
        return sb.toString();
    }

    // testing hook: override in tests to inject behavior
    protected void testingHook() { /* no-op */ }
}

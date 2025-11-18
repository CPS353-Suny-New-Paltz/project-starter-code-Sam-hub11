package apiimplementation;

import apiengine.ConceptualAPI;
import apinetwork.ComputationInput;
import apinetwork.ComputationOutput;
import apinetwork.Delimiters;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure conceptual API implementation — performs prime factorization only.
 * IMPORTANT: no ProcessAPI dependency and no IO/writes here.
 */
public class ConceptualAPIImpl implements ConceptualAPI {

    // No-arg constructor only — pure computation, no side effects.
    public ConceptualAPIImpl() {
    }

    @Override
    public ComputationOutput compute(ComputationInput input) {
        // Null input -> return "null" result (no writes).
        if (input == null) {
            return new ComputationOutput("null");
        }

        int n = input.getInputNumber();

        // Determine pair delimiter (fallback to default)
        String pairDelim = " × ";
        Delimiters d = input.getDelimiters();
        if (d != null && d.getPairDelimiter() != null) {
            pairDelim = d.getPairDelimiter();
        }

        // Base case (0 or 1)
        if (n <= 1) {
            return new ComputationOutput(String.valueOf(n));
        }

        // Compute factors and join them
        List<Integer> factors = primeFactors(n);
        String result = joinFactors(factors, pairDelim);
        return new ComputationOutput(result);
    }

    // Standard prime factorization
    private List<Integer> primeFactors(int n) {
        List<Integer> facts = new ArrayList<>();
        while (n % 2 == 0) {
            facts.add(2);
            n /= 2;
        }
        int f = 3;
        while (f * f <= n) {
            while (n % f == 0) {
                facts.add(f);
                n /= f;
            }
            f += 2;
        }
        if (n > 1) {
            facts.add(n);
        }
        return facts;
    }

    // Join integer factors to a single formatted string with delimiter
    private String joinFactors(List<Integer> factors, String delim) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < factors.size(); i++) {
            if (i > 0) {
                sb.append(delim);
            }
            sb.append(factors.get(i));
        }
        return sb.toString();
    }
}

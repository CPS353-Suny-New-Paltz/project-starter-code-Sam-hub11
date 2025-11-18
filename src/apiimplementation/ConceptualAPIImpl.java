package apiimplementation;

import apiengine.ConceptualAPI;
import apinetwork.ComputationInput;
import apinetwork.ComputationOutput;
import apinetwork.Delimiters;

import java.util.ArrayList;
import java.util.List;

public class ConceptualAPIImpl implements ConceptualAPI {

    // No-arg constructor only — pure computation, no side effects.
    public ConceptualAPIImpl() {
    }

    @Override
    public ComputationOutput compute(ComputationInput input) {
        // Defensive: handle null input
        if (input == null) {
            return new ComputationOutput("null");
        }

        int n = input.getInputNumber();

        // Base case (0 or 1)
        if (n <= 1) {
            return new ComputationOutput(String.valueOf(n));
        }

        // Determine pair delimiter (use Delimiters if provided)
        String pairDelim = " × ";
        Delimiters d = input.getDelimiters();
        if (d != null && d.getPairDelimiter() != null) {
            pairDelim = d.getPairDelimiter();
        }

        // Compute factors and join
        List<Integer> factors = primeFactors(n);
        String result = joinFactors(factors, pairDelim);
        return new ComputationOutput(result);
    }

    // Standard prime factorization
    private List<Integer> primeFactors(int n) {
        List<Integer> factors = new ArrayList<>();
        while (n % 2 == 0) {
            factors.add(2);
            n /= 2;
        }
        int f = 3;
        while (f * f <= n) {
            while (n % f == 0) {
                factors.add(f);
                n /= f;
            }
            f += 2;
        }
        if (n > 1) {
            factors.add(n);
        }
        return factors;
    }

    // join factors using pair delimiter
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

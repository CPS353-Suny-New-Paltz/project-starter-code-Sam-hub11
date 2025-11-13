package apiimplementation;

import apiengine.ConceptualAPI;
import apinetwork.ComputationInput;
import apinetwork.ComputationOutput;
import apinetwork.Delimiters;
import apistorage.ProcessAPI;

import java.util.ArrayList;
import java.util.List;

public class ConceptualAPIImpl implements ConceptualAPI {

    private final ProcessAPI processAPI; 

    public ConceptualAPIImpl() {
        this.processAPI = null;
    }

    public ConceptualAPIImpl(ProcessAPI processAPI) {
        this.processAPI = processAPI;
    }

    @Override
    public ComputationOutput compute(ComputationInput input) {
        // null input 
        if (input == null) {
            String s = "null";
            // If a ProcessAPI was provided, its "null" 
            if (processAPI != null) {
                processAPI.writeOutput(s);
            }
            return new ComputationOutput(s);
        }

        int n = input.getInputNumber();

        // Determine pair delimiter 
        String pairDelim = " × ";
        Delimiters d = input.getDelimiters();
        if (d != null && d.getPairDelimiter() != null) {
            pairDelim = d.getPairDelimiter();
        }

        // Base case
        if (n <= 1) {
            String s = String.valueOf(n);
            if (processAPI != null) {
                processAPI.writeOutput(s);
            }
            return new ComputationOutput(s);
        }

        // Compute factors and join
        List<Integer> factors = primeFactors(n);
        String result = joinFactors(factors, pairDelim);

        if (processAPI != null) {
            processAPI.writeOutput(result);
        }

        return new ComputationOutput(result);
    }

    // standard prime factorization
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


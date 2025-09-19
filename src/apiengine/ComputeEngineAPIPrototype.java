package apiengine;

import project.annotations.ConceptualAPIPrototype;
import java.util.ArrayList;
import java.util.List;
import apinetwork.ComputationInput;
import apinetwork.ComputationOutput;

public class ComputeEngineAPIPrototype {
    @ConceptualAPIPrototype
    public ComputeEngineAPI prototype(ComputeEngineAPI api) {
        return new ComputeEngineAPI() {
            @Override
            public String runComputation(int inputNumber) {
                if (inputNumber <= 1) return String.valueOf(inputNumber);

                List<Integer> factors = new ArrayList<>();
                int n = inputNumber;
                for (int i = 2; i <= n / i; i++) {
                    while (n % i == 0) {
                        factors.add(i);
                        n /= i;
                    }
                }
                if (n > 1) factors.add(n);

                return String.join(" × ", factors.stream().map(String::valueOf).toArray(String[]::new));
            }

			@Override
			public ComputationOutput runComputation(ComputationInput input) {
				// TODO Auto-generated method stub
				return null;
			}
        };
    }
}
package apiimplementation;

import apiengine.ConceptualAPI;
import apiengine.NetworkAPI;
import apinetwork.ComputationInput;
import apinetwork.ComputationOutput;
import apinetwork.JobRequest;
import apistorage.ProcessAPI;

import java.util.List;

public class NetworkAPIImpl implements NetworkAPI {
    private final ProcessAPI processAPI;
    private final ConceptualAPI conceptual;

    public NetworkAPIImpl(ProcessAPI processAPI, ConceptualAPI conceptual) {
        if (processAPI == null || conceptual == null) {
            throw new IllegalArgumentException("processAPI and conceptual must not be null");
        }
        this.processAPI = processAPI;
        this.conceptual = conceptual;
    }

    @Override
    public ComputationOutput sendJob(JobRequest job) {
        if (job == null) {
            processAPI.writeOutput("network:null-job");
            return new ComputationOutput("invalid-job");
        }

        if (job.getInputNumber() == -1) {
            List<Integer> inputs = processAPI.readInputs();
            if (inputs == null || inputs.isEmpty()) {
                processAPI.writeOutput("batch:no-inputs");
                return new ComputationOutput("batch:empty");
            }
            for (Integer v : inputs) {
                ComputationInput ci = new ComputationInput(v, job.getDelimiters());
                ComputationOutput out = conceptual.compute(ci);
                String s = (out == null || out.getResult() == null) ? "null" : out.getResult();
                processAPI.writeOutput(s);
            }
            processAPI.writeOutput("batch:completed:" + inputs.size());
            return new ComputationOutput("batch:success");
        }

        ComputationInput ci = new ComputationInput(job.getInputNumber(), job.getDelimiters());
        ComputationOutput out = conceptual.compute(ci);
        String s = (out == null || out.getResult() == null) ? "null" : out.getResult();
        processAPI.writeOutput(s);
        return out;
    }
}

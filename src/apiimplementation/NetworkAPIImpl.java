package apiimplementation;

import apiengine.ConceptualAPI;
import apiengine.NetworkAPI;
import apinetwork.ComputationInput;
import apinetwork.ComputationOutput;
import apinetwork.JobRequest;
import apistorage.ProcessAPI;

import java.util.ArrayList;
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
        // Validate job param
        if (job == null) {
            try {
                processAPI.writeOutput("network:null-job");
            } catch (Throwable t) {
                System.err.println("NetworkAPIImpl failed to write null-job marker: " + t.getMessage());
            }
            return new ComputationOutput("invalid-job");
        }

        try {
            // Batch mode: read inputs, compute each
            if (job.getInputNumber() == -1) {
                List<Integer> inputs;
                try {
                    inputs = processAPI.readInputs();
                } catch (Throwable t) {
                    try {
                        processAPI.writeOutput("batch:read-error");
                    } catch (Throwable t2) {
                        System.err.println("NetworkAPIImpl failed to write batch:read-error: " + t2.getMessage());
                    }
                    return new ComputationOutput("batch:error");
                }

                if (inputs == null || inputs.isEmpty()) {
                    try {
                        processAPI.writeOutput("batch:no-inputs");
                    } catch (Throwable t) {
                        System.err.println("NetworkAPIImpl failed to write batch:no-inputs: " + t.getMessage());
                    }
                    return new ComputationOutput("batch:empty");
                }

                // compute each result and collect them
                List<String> results = new ArrayList<>();
                for (Integer v : inputs) {
                    ComputationInput ci = new ComputationInput(v, job.getDelimiters());
                    ComputationOutput out = conceptual.compute(ci);
                    results.add(out == null || out.getResult() == null ? "null" : out.getResult());
                }

                // Write ONE comma-separated line to storage for checkpoint4 compatibility
                try {
                    processAPI.writeOutput(String.join(",", results));
                } catch (Throwable t) {
                    System.err.println("NetworkAPIImpl failed to write batch results: " + t.getMessage());
                    return new ComputationOutput("batch:error");
                }

                // Return a generic batch success marker
                return new ComputationOutput("batch:success");
            }

            // Single job validation
            if (job.getInputNumber() < 0) {
                try {
                    processAPI.writeOutput("network:invalid-input-number");
                } catch (Throwable t) {
                    System.err.println("NetworkAPIImpl failed to write network:invalid-input-number: " + t.getMessage());
                }
                return new ComputationOutput("invalid-job");
            }

            // Normal single job path
            ComputationInput ci = new ComputationInput(job.getInputNumber(), job.getDelimiters());
            ComputationOutput out = conceptual.compute(ci);
            String s = (out == null || out.getResult() == null) ? "null" : out.getResult();

            try {
                boolean ok = processAPI.writeOutput(s);
                if (!ok) {
                    try {
                        processAPI.writeOutput("network:write-failure");
                    } catch (Throwable t2) {
                        System.err.println("NetworkAPIImpl failed to write network:write-failure: " + t2.getMessage());
                    }
                    return new ComputationOutput("error:write-failed");
                }
            } catch (Throwable t) {
                try {
                    processAPI.writeOutput("network:write-failure");
                } catch (Throwable t2) {
                    System.err.println("NetworkAPIImpl failed to write network:write-failure: " + t2.getMessage());
                }
                return new ComputationOutput("error:write-failed");
            }

            return out;
        } catch (Exception ex) {
            String msg = ex.getMessage() == null ? "unknown" : ex.getMessage();
            try {
                processAPI.writeOutput("network:error:" + msg);
            } catch (Throwable t) {
                System.err.println("NetworkAPIImpl failed to write network:error: " + t.getMessage());
            }
            return new ComputationOutput("error:network-exception:" + msg);
        }
    }
}

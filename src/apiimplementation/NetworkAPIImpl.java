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
        // Validate job parameter
        if (job == null) {
            try {
                processAPI.writeOutput("network:null-job");
            } catch (Throwable t) {
                System.err.println("NetworkAPIImpl failed to write null-job marker: " + t.getMessage());
            }
            return new ComputationOutput("invalid-job");
        }

        try {
            // Batch mode: sentinel -1
            if (job.getInputNumber() == -1) {
                List<Integer> inputs;
                try {
                    inputs = processAPI.readInputs();
                } catch (Throwable t) {
                    // read error -> observable marker + error return
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

                int processed = 0;
                // compute and write EACH result as its own call to processAPI.writeOutput(...)
                for (Integer v : inputs) {
                    ComputationInput ci = new ComputationInput(v, job.getDelimiters());
                    ComputationOutput out = conceptual.compute(ci);
                    String s = (out == null || out.getResult() == null) ? "null" : out.getResult();

                    // attempt to write this one-line result
                    boolean wroteOk;
                    try {
                        wroteOk = processAPI.writeOutput(s);
                    } catch (Throwable t) {
                        // indicate write failure and return error sentinel
                        try {
                            processAPI.writeOutput("batch:write-failure");
                        } catch (Throwable t2) {
                            System.err.println("NetworkAPIImpl failed to write batch:write-failure: " + t2.getMessage());
                        }
                        return new ComputationOutput("batch:error");
                    }

                    if (!wroteOk) {
                        try {
                            processAPI.writeOutput("batch:write-failure");
                        } catch (Throwable t) {
                            System.err.println("NetworkAPIImpl failed to write batch:write-failure: " + t.getMessage());
                        }
                        return new ComputationOutput("batch:error");
                    }

                    processed++;
                }

                // after writing each result on its own line, write the completion marker
                String completedMarker = "batch:completed:" + processed;
                try {
                    boolean ok = processAPI.writeOutput(completedMarker);
                    if (!ok) {
                        System.err.println("NetworkAPIImpl storage.writeOutput returned false for batch completed marker");
                        return new ComputationOutput("batch:error");
                    }
                } catch (Throwable t) {
                    System.err.println("NetworkAPIImpl failed to write batch:completed marker: " + t.getMessage());
                    return new ComputationOutput("batch:error");
                }

                // return the completed marker as the summary output
                return new ComputationOutput(completedMarker);
            }

            // Single job path
            if (job.getInputNumber() < 0) {
                try {
                    processAPI.writeOutput("network:invalid-input-number");
                } catch (Throwable t) {
                    System.err.println("NetworkAPIImpl failed to write network:invalid-input-number: " + t.getMessage());
                }
                return new ComputationOutput("invalid-job");
            }

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

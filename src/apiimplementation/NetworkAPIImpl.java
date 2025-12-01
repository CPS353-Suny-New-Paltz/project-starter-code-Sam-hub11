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
            // Batch mode: read inputs, compute each, write each result and then a summary marker
            if (job.getInputNumber() == -1) {
                List<Integer> inputs;
                try {
                    inputs = processAPI.readInputs();
                } catch (Throwable t) {
                    // translate read error to an observable marker and return an error result
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

                // compute and write each result, collecting the written lines (for safety)
                List<String> writtenResults = new ArrayList<>();
                int processed = 0;
                for (Integer v : inputs) {
                    ComputationInput ci = new ComputationInput(v, job.getDelimiters());
                    ComputationOutput out = conceptual.compute(ci);
                    String s = (out == null || out.getResult() == null) ? "null" : out.getResult();

                    // write this result (if a write fails, propagate an error sentinel)
                    boolean wroteOk = false;
                    try {
                        wroteOk = processAPI.writeOutput(s);
                    } catch (Throwable t) {
                        // attempt to indicate the write failure to storage, then return an error
                        try {
                            processAPI.writeOutput("batch:write-failure");
                        } catch (Throwable t2) {
                            System.err.println("NetworkAPIImpl failed to write batch:write-failure: " + t2.getMessage());
                        }
                        return new ComputationOutput("batch:error");
                    }

                    if (!wroteOk) {
                        // storage reported failure — write a marker and return an error
                        try {
                            processAPI.writeOutput("batch:write-failure");
                        } catch (Throwable t) {
                            System.err.println("NetworkAPIImpl failed to write batch:write-failure: " + t.getMessage());
                        }
                        return new ComputationOutput("batch:error");
                    }

                    writtenResults.add(s);
                    processed++;
                }

                // After all results have been successfully written, write a completion marker.
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

                // Return the completion marker as the ComputationOutput summary (tests inspect writtenOutputs not the return value,
                // but returning the marker makes the behavior explicit).
                return new ComputationOutput(completedMarker);
            }

            // Single job validation
            if (job.getInputNumber() < 0) {
                try {
                    processAPI.writeOutput("network:invalid-input-number");
                } catch (Throwable t) {
                    System.err.println("NetworkAPIImpl failed to write network:invalid-input-number: " + t.getMessage())

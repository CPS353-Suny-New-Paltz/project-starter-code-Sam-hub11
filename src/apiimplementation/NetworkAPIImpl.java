package apiimplementation;

import apiengine.NetworkAPI;
import apistorage.ProcessAPI;

public class NetworkAPIImpl implements NetworkAPI {
    private final ProcessAPI processAPI;

    public NetworkAPIImpl(ProcessAPI processAPI) {
        this.processAPI = processAPI;
    }

    @Override
    public void sendJob(String job) {
        if (processAPI != null) processAPI.writeOutput("network-send:" + job);
    }
}

package apiengine;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import apiimplementation.NetworkAPIImpl;
import apiuser.UserComputeAPI;

import static org.junit.jupiter.api.Assertions.*;

public class TestNetworkAPI {
    @Test
    public void smokeTest_sendJob_noExceptions() {
        UserComputeAPI mockUser = Mockito.mock(UserComputeAPI.class);
        NetworkAPIImpl api = new NetworkAPIImpl(mockUser);

        api.sendJob("job");

        assertTrue(true);
    }
}

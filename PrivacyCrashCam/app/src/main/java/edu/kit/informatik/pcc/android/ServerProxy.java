package edu.kit.informatik.pcc.android;

import java.io.File;

import edu.kit.informatik.pcc.android.network.IClientVideoUpload;
import edu.kit.informatik.pcc.android.network.IServerConfiguration;
import edu.kit.informatik.pcc.android.network.IUserManagement;

public class ServerProxy {
    public static ServerProxy getGlobal() {
        assert global != null;
        return global;
    }

    public static void setGlobal(ServerProxy serverProxy) {
        assert global == null;
        global = serverProxy;
    }

    private static ServerProxy global;

    private IClientVideoUpload clientVideoUpload;
    private IServerConfiguration serverConfiguration;

    public void setClientVideoUpload(IClientVideoUpload clientVideoUpload) {
        assert this.clientVideoUpload == null;
        this.clientVideoUpload = clientVideoUpload;
    }

    public IClientVideoUpload getClientVideoUpload() {
        assert clientVideoUpload != null;
        return clientVideoUpload;
    }

    public void setServerConfiguration(IServerConfiguration serverConfiguration) {
        assert this.serverConfiguration == null;
        this.serverConfiguration = serverConfiguration;
    }

    public IServerConfiguration getServerConfiguration() {
        assert serverConfiguration != null;
        return serverConfiguration;
    }
}

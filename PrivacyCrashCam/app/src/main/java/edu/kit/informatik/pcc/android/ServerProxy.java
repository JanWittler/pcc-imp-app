package edu.kit.informatik.pcc.android;

import java.io.File;

import edu.kit.informatik.pcc.android.network.IClientVideoUpload;
import edu.kit.informatik.pcc.android.network.IRequestCompletion;
import edu.kit.informatik.pcc.android.network.IServerConfiguration;
import edu.kit.informatik.pcc.android.network.IUserManagement;

public class ServerProxy implements IUserManagement, IClientVideoUpload, IServerConfiguration {
    public static ServerProxy getGlobal() {
        assert global != null;
        return global;
    }

    public static void setGlobal(ServerProxy serverProxy) {
        assert global == null;
        global = serverProxy;
    }

    private static ServerProxy global;

    private IUserManagement userManagement;
    private IClientVideoUpload clientVideoUpload;
    private IServerConfiguration serverConfiguration;

    public void setUserManagement(IUserManagement userManagement) {
        assert this.userManagement == null;
        this.userManagement = userManagement;
    }

    public void setClientVideoUpload(IClientVideoUpload clientVideoUpload) {
        assert this.clientVideoUpload == null;
        this.clientVideoUpload = clientVideoUpload;
    }

    public void setServerConfiguration(IServerConfiguration serverConfiguration) {
        assert this.serverConfiguration == null;
        this.serverConfiguration = serverConfiguration;
    }

    @Override
    public void uploadVideo(File encryptedVideo, File encryptedMetadata, byte[] encryptedKey, String authenticationToken, IRequestCompletion<UploadResult> completion) {
        assertCompletelySetup();
        clientVideoUpload.uploadVideo(encryptedVideo, encryptedMetadata, encryptedKey, authenticationToken, completion);
    }

    @Override
    public void createAccount(String email, String password, IRequestCompletion<AuthenticationResult> completion) {
        assertCompletelySetup();
        userManagement.createAccount(email, password, completion);
    }

    @Override
    public void login(String email, String password, IRequestCompletion<LoginResult> completion) {
        assertCompletelySetup();
        userManagement.login(email, password, completion);
    }

    @Override
    public String scheme() {
        assertCompletelySetup();
        return serverConfiguration.scheme();
    }

    @Override
    public String host() {
        assertCompletelySetup();
        return serverConfiguration.host();
    }

    @Override
    public String path() {
        assertCompletelySetup();
        return serverConfiguration.path();
    }

    @Override
    public int port() {
        assertCompletelySetup();
        return serverConfiguration.port();
    }

    private void assertCompletelySetup() {
        assert userManagement != null;
        assert clientVideoUpload != null;
        assert serverConfiguration != null;
    }
}

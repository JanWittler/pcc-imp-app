package edu.kit.informatik.pcc.android;

import java.io.File;

import edu.kit.informatik.pcc.android.account.ISessionManager;
import edu.kit.informatik.pcc.android.account.SessionManager;
import edu.kit.informatik.pcc.android.settings.ISettingsManager;
import edu.kit.informatik.pcc.android.storage.video.ILocalVideoManager;
import edu.kit.informatik.pcc.android.storage.video.IVideoDetailsProvider;
import edu.kit.informatik.pcc.core.data.FileSystemManager;
import edu.kit.informatik.pcc.core.data.IFileManager;

public class Client {
    public static Client getGlobal() {
        if (_global == null) {
            setupClient();
        }
        return _global;
    }

    private static Client _global;

    private ISessionManager sessionManager;
    private ILocalVideoManager localVideoManager;
    private IVideoDetailsProvider videoDetailsProvider;
    private IFileManager temporaryFileManager;
    private ISettingsManager settingsManager;

    public void setSessionManager(ISessionManager sessionManager) {
        assert this.sessionManager == null;
        this.sessionManager = sessionManager;
    }

    public ISessionManager getSessionManager() {
        assert sessionManager != null;
        return sessionManager;
    }

    public void setLocalVideoManager(ILocalVideoManager localVideoManager) {
        assert this.localVideoManager == null;
        this.localVideoManager = localVideoManager;
    }

    public ILocalVideoManager getLocalVideoManager() {
        assert localVideoManager != null;
        return localVideoManager;
    }

    public void setVideoDetailsProvider(IVideoDetailsProvider videoDetailsProvider) {
        assert this.videoDetailsProvider == null;
        this.videoDetailsProvider = videoDetailsProvider;
    }

    public IVideoDetailsProvider getVideoDetailsProvider() {
        assert videoDetailsProvider != null;
        return videoDetailsProvider;
    }

    public void setTemporaryFileManager(IFileManager temporaryFileManager) {
        assert this.temporaryFileManager == null;
        this.temporaryFileManager = temporaryFileManager;
    }

    public IFileManager getTemporaryFileManager() {
        assert temporaryFileManager != null;
        return temporaryFileManager;
    }

    public void setSettingsManager(ISettingsManager settingsManager) {
        assert this.settingsManager == null;
        this.settingsManager = settingsManager;
    }

    public ISettingsManager getSettingsManager() {
        assert settingsManager != null;
        return settingsManager;
    }

    public static void setupClient() {
        FileSystemManager sessionFileManager = new FileSystemManager("session");

        Client client = new Client();

        SessionManager sessionManager = new SessionManager();
        sessionManager.setFileManager(sessionFileManager);

        client.setSessionManager(sessionManager);

        _global = client;
    }
}

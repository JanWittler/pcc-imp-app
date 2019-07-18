package edu.kit.informatik.pcc.android;

import java.io.File;

import edu.kit.informatik.pcc.android.account.ISessionManager;
import edu.kit.informatik.pcc.android.account.SessionManager;
import edu.kit.informatik.pcc.android.network.IClientVideoUpload;
import edu.kit.informatik.pcc.android.network.IRequestCompletion;
import edu.kit.informatik.pcc.android.network.IUserManagement;
import edu.kit.informatik.pcc.android.storage.video.ILocalVideoManager;
import edu.kit.informatik.pcc.android.storage.video.IVideoDetailsProvider;
import edu.kit.informatik.pcc.core.data.FileSystemManager;

public class Client implements ISessionManager, ILocalVideoManager, IVideoDetailsProvider {
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

    public void setSessionManager(ISessionManager sessionManager) {
        assert this.sessionManager == null;
        this.sessionManager = sessionManager;
    }

    public void setLocalVideoManager(ILocalVideoManager localVideoManager) {
        assert this.localVideoManager == null;
        this.localVideoManager = localVideoManager;
    }

    public void setVideoDetailsProvider(IVideoDetailsProvider videoDetailsProvider) {
        assert this.videoDetailsProvider == null;
        this.videoDetailsProvider = videoDetailsProvider;
    }

    public static void setupClient() {
        FileSystemManager sessionFileManager = new FileSystemManager("session");

        Client client = new Client();

        SessionManager sessionManager = new SessionManager();
        sessionManager.setFileManager(sessionFileManager);

        client.setSessionManager(sessionManager);

        _global = client;
    }

    @Override
    public void storeAuthenticationToken(String authenticationToken) {
        assertCompletelySetup();
        sessionManager.storeAuthenticationToken(authenticationToken);
    }

    @Override
    public String loadAuthenticationToken() {
        assertCompletelySetup();
        return sessionManager.loadAuthenticationToken();
    }

    @Override
    public void storeVideo(File video, File metadata) {
        assertCompletelySetup();
        localVideoManager.storeVideo(video, metadata);
    }

    @Override
    public int[] getLocallyStoredVideoIds() {
        assertCompletelySetup();
        return localVideoManager.getLocallyStoredVideoIds();
    }

    @Override
    public void deleteContentForVideo(int videoId) {
        assertCompletelySetup();
        localVideoManager.deleteContentForVideo(videoId);
    }

    @Override
    public File getMetadata(int videoId) {
        assertCompletelySetup();
        return localVideoManager.getMetadata(videoId);
    }

    @Override
    public File getEncryptedVideo(int videoId) {
        assertCompletelySetup();
        return videoDetailsProvider.getEncryptedVideo(videoId);
    }

    @Override
    public File getEncryptedMetadata(int videoId) {
        assertCompletelySetup();
        return videoDetailsProvider.getEncryptedMetadata(videoId);
    }

    @Override
    public byte[] getEncryptedKey(int videoId) {
        assertCompletelySetup();
        return videoDetailsProvider.getEncryptedKey(videoId);
    }

    private void assertCompletelySetup() {
        assert sessionManager != null;
        assert localVideoManager != null;
        assert videoDetailsProvider != null;
    }
}

package edu.kit.informatik.pcc.android.account;

import edu.kit.informatik.pcc.android.storage.video.ILocalVideoManager;

public class SessionManager implements ISessionManager {
    private ISessionManager sessionStorage;
    private ILocalVideoManager localVideoManager;

    public void setSessionStorage(ISessionManager sessionStorage) {
        assert this.sessionStorage == null;
        this.sessionStorage = sessionStorage;
    }

    public void setLocalVideoManager(ILocalVideoManager localVideoManager) {
        assert this.localVideoManager == null;
        this.localVideoManager = localVideoManager;
    }

    @Override
    public void storeSessionToken(String sessionToken) {
        assertCompletelySetup();
        sessionStorage.storeSessionToken(sessionToken);
    }

    @Override
    public String loadSessionToken() {
        assertCompletelySetup();
        return sessionStorage.loadSessionToken();
    }

    @Override
    public void deleteSession() {
        assertCompletelySetup();
        for (int videoId: localVideoManager.getLocallyStoredVideoIds()) {
            localVideoManager.deleteContentForVideo(videoId);
        }
        sessionStorage.deleteSession();
    }

    private void assertCompletelySetup() {
        assert sessionStorage != null;
        assert localVideoManager != null;
    }
}

package edu.kit.informatik.pcc.android.account;

import edu.kit.informatik.pcc.android.storage.ISimpleValueStorage;

public class SessionStorage implements ISessionManager {
    private final static String tokenKey = SessionStorage.class.getName() + ".token";

    private ISimpleValueStorage simpleValueStorage;

    public void setSimpleValueStorage(ISimpleValueStorage simpleValueStorage) {
        assert this.simpleValueStorage == null;
        this.simpleValueStorage = simpleValueStorage;
    }

    @Override
    public void storeSessionToken(String sessionToken) {
        assertCompletelySetup();
        simpleValueStorage.putString(tokenKey, sessionToken);
    }

    @Override
    public String loadSessionToken() {
        assertCompletelySetup();
        return simpleValueStorage.getString(tokenKey);
    }

    @Override
    public void deleteSession() {
        storeSessionToken(null);
    }

    private void assertCompletelySetup() {
        assert simpleValueStorage != null;
    }
}

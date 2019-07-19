package edu.kit.informatik.pcc.android.account;

import edu.kit.informatik.pcc.android.storage.ISimpleValueStorage;

public class SessionManager implements ISessionManager {
    private final static String tokenKey = SessionManager.class.getName() + ".token";

    private ISimpleValueStorage simpleValueStorage;

    public void setSimpleValueStorage(ISimpleValueStorage simpleValueStorage) {
        assert this.simpleValueStorage == null;
        this.simpleValueStorage = simpleValueStorage;
    }

    @Override
    public void storeAuthenticationToken(String authenticationToken) {
        assertCompletelySetup();
        simpleValueStorage.putString(tokenKey, authenticationToken);
    }

    @Override
    public String loadAuthenticationToken() {
        assertCompletelySetup();
        return simpleValueStorage.getString(tokenKey);
    }

    private void assertCompletelySetup() {
        assert simpleValueStorage != null;
    }
}

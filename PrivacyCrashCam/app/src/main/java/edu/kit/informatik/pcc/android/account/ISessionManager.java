package edu.kit.informatik.pcc.android.account;

public interface ISessionManager {
    public void storeSessionToken(String sessionToken);
    public String loadSessionToken();
    public void deleteSession();
}

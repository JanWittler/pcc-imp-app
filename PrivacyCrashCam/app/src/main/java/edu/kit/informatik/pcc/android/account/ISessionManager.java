package edu.kit.informatik.pcc.android.account;

public interface ISessionManager {
    public void storeAuthenticationToken(String authenticationToken);
    public String loadAuthenticationToken();
}

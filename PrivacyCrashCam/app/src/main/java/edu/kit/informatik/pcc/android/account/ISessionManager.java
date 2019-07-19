package edu.kit.informatik.pcc.android.account;

import edu.kit.informatik.pcc.android.network.IRequestCompletion;
import edu.kit.informatik.pcc.android.network.IUserManagement;

public interface ISessionManager {
    void login(String email, String password, final IRequestCompletion<IUserManagement.AuthenticationResult> completion);
    Boolean hasActiveSession();
    String getAuthenticationToken();
    void logout();
}

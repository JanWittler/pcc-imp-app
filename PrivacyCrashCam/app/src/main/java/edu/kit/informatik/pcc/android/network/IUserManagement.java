package edu.kit.informatik.pcc.android.network;

public interface IUserManagement {
    void createAccount(String email, String password, IRequestCompletion<AuthenticationResult> completion);
    void login(String email, String password, IRequestCompletion<LoginResult> completion);

    enum AuthenticationResult {
        //! The authentication was successful
        SUCCESS,

        //! Network not reachable
        FAILURE_NETWORK,

        //! Account does not exist or mail and password do not match
        FAILURE_MISMATCH,

        //! Account does not exist or mail and password do not match
        FAILURE_MISSING,

        //! Something unexpected went wrong
        FAILURE_OTHER
    }

    class LoginResult {
        public String authenticationToken;
        public AuthenticationResult result;
    }
}

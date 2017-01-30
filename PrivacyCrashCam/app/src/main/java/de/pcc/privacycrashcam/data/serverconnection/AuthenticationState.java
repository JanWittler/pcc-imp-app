package de.pcc.privacycrashcam.data.serverconnection;

/**
 * Authentication state enum to determine if a server request for authenticating a user was
 * successful.
 *
 * @author Giorgio Gross
 */
public enum AuthenticationState {

    /**
     * The authentication was successful
     */
    SUCCESS,

    /**
     * Account does not exist or mail and password do not match
     */
    FAILURE_MISMATCH,

    /**
     * Something unexpected went wrong
     */
    FAILURE_OTHER;
}

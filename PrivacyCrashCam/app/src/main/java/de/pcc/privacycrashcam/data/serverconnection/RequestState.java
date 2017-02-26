package de.pcc.privacycrashcam.data.serverconnection;

/**
 * Request state enum to determine if a server request for authenticating a user was
 * successful.
 *
 * @author Fabian Wenzel
 */
public enum RequestState {

    /**
     * The request was successful.
     */
    SUCCESS,

    /**
     * Network not reachable
     */
    NETWORK_FAILURE,

    /**
     * The MultiPartForm had not the right input for this kind of request.
     */
    INPUT_FAILURE,

    /**
     * An error occurred editing the video.
     */
    EDITING_FAILURE,

    /**
     * Account data outdated.
     */
    ACCOUNT_FAILURE,

    /**
     * Something unexpected went wrong.
     */
    FAILURE_OTHER
}

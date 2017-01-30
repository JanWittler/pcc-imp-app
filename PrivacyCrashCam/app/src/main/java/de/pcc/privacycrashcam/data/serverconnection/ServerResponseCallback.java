package de.pcc.privacycrashcam.data.serverconnection;

/**
 * Observer for events occurring when communicating with the server.
 *
 * author Giorgio Gross
 */
public interface ServerResponseCallback<E> {

    /**
     * Called when the response was received. The request was completed.
     * @param response the response of the server
     */
    void onResponse(E response);

    /**
     * Called whenever a request makes some progress.
     *
     * @param percent percentage of the work which was done
     */
    void onProgress(int percent);

    /**
     * Called when an error occurred while establishing the connection to the server.
     *
     * @param error Error message - not intended to be readable for user
     */
    void onError(String error);

}

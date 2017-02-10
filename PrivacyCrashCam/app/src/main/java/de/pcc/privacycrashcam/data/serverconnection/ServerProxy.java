package de.pcc.privacycrashcam.data.serverconnection;

import java.io.File;

import de.pcc.privacycrashcam.data.Account;

/**
 * Proxy representing the server interface.
 *
 * @author Giorgio Gross
 */

public class ServerProxy {
    /**
     * Domain to connect to the API
     */
    private static final String URL = "http://laubenstone.de:2222/webservice/";

    public ServerProxy () {

    }

    public void videoUpload(File videoFile, File metadata, File symKey, Account account,
                            ServerResponseCallback<RequestState> callback) {
        new VideoUploadTask(videoFile, metadata, symKey, account, callback).execute(URL);
    }

    public void authenticateUser(Account account,
                                 ServerResponseCallback<AuthenticationState> callback) {
        new AuthenticateTask(account, callback).execute(URL);
    }

}

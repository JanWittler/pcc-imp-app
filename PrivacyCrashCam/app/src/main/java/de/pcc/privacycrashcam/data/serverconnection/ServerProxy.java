package de.pcc.privacycrashcam.data.serverconnection;

import android.content.Context;

import java.io.File;

import de.pcc.privacycrashcam.data.Account;

/**
 * Proxy representing the server interface.
 *
 * @author Giorgio Gross
 */

public class ServerProxy {
    private Context context;

    public ServerProxy(Context context) {
        this.context = context;
    }

    /**
     * Uploads the passed video along with the passed metadata and key to the account
     *
     * @param videoFile Video to be uploaded
     * @param metadata  Metadata of the video
     * @param symKey    Key of the video and metadata
     * @param account   Account to link the files with
     * @param callback  Observer which will be notified about state changes of the upload
     */
    public void videoUpload(final File videoFile, final File metadata,
                            final File symKey, final Account account,
                            final ServerResponseCallback<RequestState> callback) {
        // check if password and mail are still valid
        authenticateUser(account, new ServerResponseCallback<AuthenticationState>() {
            @Override
            public void onResponse(AuthenticationState response) {
                if (response == AuthenticationState.SUCCESS) {
                    // we are logged in - proceed with file upload
                    new VideoUploadTask(videoFile, metadata, symKey, account, callback, context)
                            .execute(ServerHelper.URL);
                } else {
                    callback.onResponse(RequestState.ACCOUNT_FAILURE);
                }
            }

            @Override
            public void onProgress(int percent) {
                // ignored
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    /**
     * Authenticate the user. This will check if the password and mail match.
     *
     * @param account  Account which needs to be authenticated.
     * @param callback Observer which will be notified about state changes of the authentication
     */
    public void authenticateUser(Account account,
                                 ServerResponseCallback<AuthenticationState> callback) {
        new AuthenticateTask(account, callback, context).execute(ServerHelper.URL);
    }
}

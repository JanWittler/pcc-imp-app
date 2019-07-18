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
        new VideoUploadTask(videoFile, metadata, symKey, account, callback, context)
                .execute(ServerHelper.URL);
    }
}

package de.pcc.privacycrashcam.data.serverconnection;

import android.os.AsyncTask;

import java.io.File;

import de.pcc.privacycrashcam.data.Account;

/**
 * Task to asynchronously upload video files of the user. This class already knows hwo to pass the
 * params to the REST interface, the API method call and how to parse the result.
 *
 * @author Giorgio Gross
 */
public class VideoUploadTask extends AsyncTask<String, Integer, RequestState> {
    private final static String TAG = VideoUploadTask.class.getName();
    /**
     * Function call which will be appended to the domain name
     */
    private static final String API_CALL = "verifyAccount";

    private Account account;
    private ServerResponseCallback<RequestState> callback;
    private File videoFile;
    private File metadata;
    private File symKey;

    /**
     * Sets up a new task to upload the video data with the passed parameters
     *
     * @param videoFile file pointing to the video to upload
     * @param metadata  file pointing to the metadata of the video
     * @param symKey    asymmetric encrypted key used to encrypt video and metadata
     * @param account   Account which will be used for upload
     * @param callback  Observer which is notified about errors and state changes
     */
    public VideoUploadTask(File videoFile, File metadata, File symKey, Account account,
                           ServerResponseCallback<RequestState> callback) {
        this.videoFile = videoFile;
        this.metadata = metadata;
        this.symKey = symKey;
        this.account = account;
        this.callback = callback;
    }

    @Override
    protected RequestState doInBackground(String... params) {
        return null;
    }

    @Override
    protected void onPostExecute(RequestState requestState) {
        super.onPostExecute(requestState);
        callback.onResponse(requestState);
    }
}

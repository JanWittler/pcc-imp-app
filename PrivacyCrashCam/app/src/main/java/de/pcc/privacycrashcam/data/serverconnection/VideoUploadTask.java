package de.pcc.privacycrashcam.data.serverconnection;

import android.os.AsyncTask;
import android.util.Log;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.pcc.privacycrashcam.data.Account;

/**
 * Task to asynchronously upload video files of the user. This class already knows hwo to pass the
 * params to the REST interface, the API method call and how to parse the result.
 *
 * @author Fabian Wenzel
 */
public class VideoUploadTask extends AsyncTask<String, Integer, RequestState> {
    private final static String TAG = VideoUploadTask.class.getName();
    /**
     * Function call which will be appended to the domain name
     */
    private static final String API_CALL = "videoUpload";

    private static final String API_RESPONSE_SUCCESS = "Finished editing video";
    private static final String API_RESPONSE_INPUT_FAILURE = "Not all inputs were given correctly";
    private static final String API_RESPONSE_EDITING_FAILURE = "Setting up for editing video failed. Processing aborted";

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

    /**
     * @param params Domain to access the API
     * @return {@link RequestState}
     */
    @Override
    protected RequestState doInBackground(String... params) {
        if (!ServerHelper.isOnline()) {
            callback.onError("No connection to HOST");
            return null;
        }
        RequestState requestState;
        String domain = params[0];

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(domain).path(API_CALL).register(MultiPartFeature.class);
        Log.i(TAG, "URI: " + webTarget.getUri().toASCIIString());
        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        FileDataBodyPart video = new FileDataBodyPart("video", videoFile.getAbsoluteFile(), MediaType.APPLICATION_OCTET_STREAM_TYPE);
        FileDataBodyPart metadata = new FileDataBodyPart("metadata", this.metadata.getAbsoluteFile(), MediaType.APPLICATION_OCTET_STREAM_TYPE);
        FileDataBodyPart key = new FileDataBodyPart("key", symKey.getAbsoluteFile(), MediaType.APPLICATION_OCTET_STREAM_TYPE);
        FormDataBodyPart data = new FormDataBodyPart("account", account.getAsJSON());
        multiPart.bodyPart(video);
        multiPart.bodyPart(metadata);
        multiPart.bodyPart(key);
        multiPart.bodyPart(data);
        Future<Response> futureResponse = webTarget.request().async().post(Entity.entity(multiPart, multiPart.getMediaType()), Response.class);
        String responseContent = "";
        try {
            Response response = futureResponse.get();
            responseContent = response.readEntity(String.class);
        } catch (InterruptedException | ExecutionException e) {
            Log.i(TAG, "Failure on getting response!");
            return RequestState.FAILURE_OTHER;
        }
        Log.i(TAG, "response: " + responseContent);

        switch (responseContent) {
            case API_RESPONSE_SUCCESS:
                requestState = RequestState.SUCCESS;
                break;
            case API_RESPONSE_EDITING_FAILURE:
                requestState = RequestState.EDITING_FAILURE;
                break;
            case API_RESPONSE_INPUT_FAILURE:
                requestState = RequestState.INPUT_FAILURE;
                break;
            default:
                requestState = RequestState.FAILURE_OTHER;
        }
        return requestState;
    }

    /**
     * @param requestState
     */
    @Override
    protected void onPostExecute(RequestState requestState) {
        super.onPostExecute(requestState);
        callback.onResponse(requestState);
    }
}

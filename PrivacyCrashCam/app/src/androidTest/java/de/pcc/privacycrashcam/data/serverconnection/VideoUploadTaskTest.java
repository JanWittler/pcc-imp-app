package de.pcc.privacycrashcam.data.serverconnection;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import de.pcc.privacycrashcam.BaseTest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by Fabi on 11.03.2017.
 * @author Fabian Wenzel
 */
public class VideoUploadTaskTest extends BaseTest {

    private VideoUploadTask videoUploadTask;
    private final String ADDRESS = "http://laubenstone.de:2222/webservice/";
    private final String JSON_KEY_MAIL = "mail";
    private final String JSON_KEY_PASSWORD = "password";
    private JSONObject json;
    private File video;
    private File metadata;
    private File symKey;

    private RequestState onResponse;
    private String onError;
    private ServerResponseCallback serverResponseCallback = new ServerResponseCallback() {
        @Override
        public void onResponse(Object response) {
            onResponse = (RequestState) response;
        }

        @Override
        public void onProgress(int percent) {

        }

        @Override
        public void onError(String error) {
            onError = error;
        }
    };


    @Before
    public void setUp() {
        videoUploadTask = new VideoUploadTask(video, metadata, symKey, accountMock, serverResponseCallback, context);
    }

    @Test
    @Ignore
    public void FailureOtherTest() {
        video = new File("C:\\Informatik-Studium\\PSE\\git\\pcc-imp-app\\PrivacyCrashCam\\app\\src\\androidTest\\resources\\input.mp4");
        metadata = new File("C:\\Informatik-Studium\\PSE\\git\\pcc-imp-app\\PrivacyCrashCam\\app\\src\\androidTest\\resources\\input.mp4");
        symKey = new File("C:\\Informatik-Studium\\PSE\\git\\pcc-imp-app\\PrivacyCrashCam\\app\\src\\androidTest\\resources\\input.mp4");
        try {
            json = new JSONObject("{}");
            json.put(JSON_KEY_MAIL, "failMail@321.de");
            json.put(JSON_KEY_PASSWORD, "failPW");
        } catch (JSONException e) {
            Assert.fail();
        }
        when(accountMock.getAsJSON()).thenReturn(json.toString());
        videoUploadTask = new VideoUploadTask(video, metadata, symKey, accountMock, serverResponseCallback, context);
        RequestState requestState = videoUploadTask.doInBackground(ADDRESS);
        Assert.assertTrue(requestState == RequestState.FAILURE_OTHER);
    }

    @Test
    @Ignore
    public void InputFailureTest() {
        video = new File("");
        metadata = new File("");
        symKey = new File("");
        videoUploadTask = new VideoUploadTask(video, metadata, symKey, accountMock, serverResponseCallback, context);
        try {
            json = new JSONObject("{}");
            json.put(JSON_KEY_MAIL, "test123@321.de");
            json.put(JSON_KEY_PASSWORD, "123456");
        } catch (JSONException e) {
            Assert.fail();
        }
        RequestState requestState = videoUploadTask.doInBackground(ADDRESS);
        Assert.assertTrue(requestState == RequestState.INPUT_FAILURE);
    }

    @Test
    public void NoNetworkTest() {
        videoUploadTask.onPostExecute(RequestState.NETWORK_FAILURE);
        Assert.assertTrue(onError.equals("No network available"));
    }

    @Test
    public void onPostExecuteSuccessTest() {
        videoUploadTask.onPostExecute(RequestState.SUCCESS);
        Assert.assertTrue(onResponse == RequestState.SUCCESS);
    }

}
package de.pcc.privacycrashcam.data.serverconnection;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.pcc.privacycrashcam.BaseTest;

import static org.mockito.Mockito.when;

/**
 * Created by Fabi on 11.03.2017.
 * @author Fabian Wenzel
 */
public class AuthenticateTaskTest extends BaseTest {

    private AuthenticateTask authenticateTask;
    private final String ADDRESS = "http://laubenstone.de:2222/webservice/";
    private final String JSON_KEY_MAIL = "mail";
    private final String JSON_KEY_PASSWORD = "password";
    private JSONObject json;

    private AuthenticationState onResponse;
    private String onError;
    private ServerResponseCallback serverResponseCallback = new ServerResponseCallback() {
        @Override
        public void onResponse(Object response) {
            onResponse = (AuthenticationState) response;
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
        authenticateTask = new AuthenticateTask(accountMock, serverResponseCallback, context);
    }

    @Test
    public void MissingTest() {
        try {
            json = new JSONObject("{}");
            json.put(JSON_KEY_MAIL, "failMail@321.de");
            json.put(JSON_KEY_PASSWORD, "failPW");
        } catch (JSONException e) {
            Assert.fail();
        }
        when(accountMock.getAsJSON()).thenReturn(json.toString());
        AuthenticationState state = authenticateTask.doInBackground(ADDRESS);
        Assert.assertTrue(state == AuthenticationState.FAILURE_MISSING);
    }

    @Test
    public void SuccessTest() {
        try {
            json = new JSONObject("{}");
            json.put(JSON_KEY_MAIL, "test123@321.de");
            json.put(JSON_KEY_PASSWORD, "123456");
        } catch (JSONException e) {
            Assert.fail();
        }
        when(accountMock.getAsJSON()).thenReturn(json.toString());
        AuthenticationState state = authenticateTask.doInBackground(ADDRESS);
        Assert.assertTrue(state == AuthenticationState.SUCCESS);
    }

    @Test
    public void NotVerifiedTest() {
        try {
            json = new JSONObject("{}");
            json.put(JSON_KEY_MAIL, "notVerified@not.de");
            json.put(JSON_KEY_PASSWORD, "123456");
        } catch (JSONException e) {
            Assert.fail();
        }
        when(accountMock.getAsJSON()).thenReturn(json.toString());
        AuthenticationState state = authenticateTask.doInBackground(ADDRESS);
        Assert.assertTrue(state == AuthenticationState.NOT_VERIFIED);
    }

    @Test
    public void MissmatchTest() {
        try {
            json = new JSONObject("{}");
            json.put(JSON_KEY_MAIL, "test123@321.de");
            json.put(JSON_KEY_PASSWORD, "wrongPW");
        } catch (JSONException e) {
            Assert.fail();
        }
        when(accountMock.getAsJSON()).thenReturn(json.toString());
        AuthenticationState state = authenticateTask.doInBackground(ADDRESS);
        Assert.assertTrue(state == AuthenticationState.FAILURE_MISMATCH);
    }

    @Test
    public void FailureOtherTest() {
        when(accountMock.getAsJSON()).thenReturn(null);
        AuthenticationState state = authenticateTask.doInBackground(ADDRESS);
        Assert.assertTrue(state == AuthenticationState.FAILURE_OTHER);
    }

    @Test
    public void NoNetworkTest() {
        authenticateTask.onPostExecute(AuthenticationState.FAILURE_NETWORK);
        Assert.assertTrue(onError.equals("No network available"));
    }

    @Test
    public void RequestTest() {
        authenticateTask.onPostExecute(AuthenticationState.SUCCESS);
        Assert.assertTrue(onResponse == AuthenticationState.SUCCESS);
    }

}
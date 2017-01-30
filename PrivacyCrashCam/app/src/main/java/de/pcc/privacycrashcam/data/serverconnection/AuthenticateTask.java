package de.pcc.privacycrashcam.data.serverconnection;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.pcc.privacycrashcam.data.Account;

/**
 * Task to asynchronously authenticate the user. This class already knows hwo to pass the params to
 * the REST interface, the API method call and how to parse the result.
 *
 * @author Giorgio Gross
 */
public class AuthenticateTask extends AsyncTask<String, Integer, AuthenticationState> {
    private final static String TAG = AuthenticateTask.class.getName();

    /**
     * Function call which will be appended to the domain name
     */
    private static final String API_CALL = "authenticate/";
    // responses to be expected
    private static final String API_RESPONSE_FAILURE_OTHER = "FAILURE";
    private static final String API_RESPONSE_FAILURE_MISSING = "NO ACCOUNTID";
    private static final String API_RESPONSE_FAILURE_MISMATCH = "WRONG ACCOUNT";
    private static final String API_RESPONSE_SUCCESS = "SUCCESS";

    private Account account;
    private ServerResponseCallback<AuthenticationState> callback;

    /**
     * Sets up a new task to authenticate the user with the passed parameters
     *
     * @param account   Account which will be used for upload
     * @param callback  Observer which is notified about errors and state changes
     */
    public AuthenticateTask(Account account, ServerResponseCallback<AuthenticationState> callback) {
        this.account = account;
        this.callback = callback;
    }

    /**
     * @param params Domain to access the API
     * @return {@link AuthenticationState}
     */
    @Override
    protected AuthenticationState doInBackground(String... params) {
        AuthenticationState resultState;
        String domain = params[0];

        Form form = new Form();
        form.param("data", account.getAsJSON());
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(domain).path("webservice").path("authenticate");
        Log.i(TAG, "URI: " + webTarget.getUri().toASCIIString());
        Response response = webTarget.request().post(Entity.entity(form,
                MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
        String responseContent = response.readEntity(String.class);
        Log.i(TAG, "response: " + responseContent);

        switch (responseContent) {
            case API_RESPONSE_FAILURE_MISSING:
                resultState = AuthenticationState.FAILURE_MISSING;
                break;
            case API_RESPONSE_FAILURE_MISMATCH:
                resultState = AuthenticationState.FAILURE_MISMATCH;
                break;
            case API_RESPONSE_SUCCESS:
                resultState = AuthenticationState.SUCCESS;
                break;
            default:
                resultState = AuthenticationState.FAILURE_OTHER;
                break;
        }

        return resultState;
    }

    @Override
    protected void onPostExecute(AuthenticationState requestState) {
        super.onPostExecute(requestState);
        callback.onResponse(requestState);
    }

}

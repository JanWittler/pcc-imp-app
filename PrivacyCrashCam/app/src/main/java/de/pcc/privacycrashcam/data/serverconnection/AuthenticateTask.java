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
    private static final String API_RESPONSE_FAILURE_MISMATCH = "WRONG ACCOUNT";
    private static final String API_RESPONSE_SUCCESS = "SUCCESS";

    private static final int TIME_OUT = 20000; // 20sec time out

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
        AuthenticationState resultState = AuthenticationState.FAILURE_OTHER; // default value
        String domain = params[0];
        InputStream is = null;

        // log account - todo remove this log message later
        try {
            Log.i("SERVER", "json query: " + new JSONObject(account.getAsJSON()).toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            // set up connection
            URL url = new URL(domain + API_CALL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            // write data to the output stream
            OutputStream os = conn.getOutputStream();
            BufferedWriter printout = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            printout.write("data="+account.getAsJSON()); // todo edit param
            printout.flush();
            printout.close();

            int response = conn.getResponseCode();
            Log.i(TAG, "Server Response: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String result = ServerHelper.readInputStream(is);

            switch (result) {
                case API_RESPONSE_FAILURE_OTHER:
                    resultState = AuthenticationState.FAILURE_OTHER;
                    break;
                case API_RESPONSE_FAILURE_MISMATCH:
                    resultState = AuthenticationState.FAILURE_MISMATCH;
                    break;
                case API_RESPONSE_SUCCESS:
                    resultState = AuthenticationState.SUCCESS;
                    break;
            }
        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultState;
    }

    @Override
    protected void onPostExecute(AuthenticationState requestState) {
        super.onPostExecute(requestState);
        callback.onResponse(requestState);
    }

}

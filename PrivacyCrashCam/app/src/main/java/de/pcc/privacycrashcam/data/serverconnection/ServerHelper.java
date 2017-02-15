package de.pcc.privacycrashcam.data.serverconnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * static helper methods for server connection
 *
 * @author Giorgio Gross
 */

public class ServerHelper {
    private final static String TAG = ServerHelper.class.getName();
    public static final String HOST = "http://laubenstone.de:2222";
    public static final String URL = HOST + "/webservice/";

    /**
     * Synchronous method which pings the server and checks if it is reachable. Do not use this on
     * UI thread.
     *
     * @param context The app context
     * @return true when network was reached, false otherwise
     */
    public static boolean IsNetworkAvailable (Context context) {
        ConnectivityManager mConnManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isTheoreticallyAvailable = (mConnManager.getActiveNetworkInfo() != null
                && mConnManager.getActiveNetworkInfo().isConnected()
                && mConnManager.getActiveNetworkInfo().isConnectedOrConnecting());

        if(!isTheoreticallyAvailable)
            return false;

        try {
            java.net.URL mUrl = new URL(HOST);
            HttpURLConnection mUrlConn = (HttpURLConnection) (mUrl).openConnection();
            mUrlConn.setRequestProperty("User-Agent", "Test");
            mUrlConn.setRequestProperty("Connection", "close");
            mUrlConn.setConnectTimeout(1500);
            mUrlConn.connect();
            return (mUrlConn.getResponseCode() >= 200
                    && mUrlConn.getResponseCode() <= 299);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

}

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
    /**
     * Domain to connect to the API
     */
    public static final String PORT = ":2222";
    public static final String HOST = "http://laubenstone.de";
    public static final String URL = HOST + PORT + "/webservice/";

    /**
     * Synchronous method which pings the server and checks if it is reachable. Do not use this on
     * UI thread.
     *
     * @param context The app context
     * @return true when network was reached, false otherwise
     */
    public static boolean IsNetworkAvailable (Context context) {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

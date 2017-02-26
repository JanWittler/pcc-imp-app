package de.pcc.privacycrashcam.data.serverconnection;

/**
 * Static helper methods for server connection
 *
 * @author Giorgio Gross
 */

class ServerHelper {
    private final static String TAG = ServerHelper.class.getName();
    /**
     * Domain to connect to the API
     */
    static final String PORT = ":2222";
    static final String HOST = "http://laubenstone.de";
    static final String URL = HOST + PORT + "/webservice/";

    /**
     * Synchronous method which pings the server and checks if it is reachable. Do not use this on
     * UI thread.
     *
     * @return true when network was reached, false otherwise
     */
    static boolean IsNetworkAvailable() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 google.com");
            int returnVal = p1.waitFor();
            return (returnVal==0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

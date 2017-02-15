package de.pcc.privacycrashcam.data.serverconnection;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * @author Giorgio Gross
 */

public class ServerHelper {
    private final static String TAG = ServerHelper.class.getName();
    public static final String HOST = "http://laubenstone.de:2222";
    public static final String URL = HOST + "/webservice/";

    /**
     *
     * @return
     */
    public static boolean isOnline() {

//        Runtime runtime = Runtime.getRuntime();
//        try {
//
//            Process ipProcess = runtime.exec("/system/bin/ping -c 1 " + HOST);
//            int     exitValue = ipProcess.waitFor();
//            return (exitValue == 0);
//
//        } catch (IOException e) { e.printStackTrace(); }
//        catch (InterruptedException e) { e.printStackTrace(); }

        return true;
    }
}

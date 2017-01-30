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

    /**
     * Reads an input stream and converts it to a string.
     *
     * @param stream The input stream read from the server response
     * @return The input stream as a String
     */
    public static String readInputStream(InputStream stream) throws IOException {
        if(stream == null) return ""; // default return value

        Scanner s = new Scanner(stream, "UTF-8");
        StringBuilder mBuffer = new StringBuilder();
        try {
            while (s.hasNext()) {
                mBuffer.append(s.next());
                mBuffer.append(" ");
            }
        } finally{
            s.close();
            Log.i("BUFFER", "the read buffer is " + mBuffer.toString());
        }
        return mBuffer.toString();
    }

}

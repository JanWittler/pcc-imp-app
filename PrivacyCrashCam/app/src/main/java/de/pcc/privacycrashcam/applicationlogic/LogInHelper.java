package de.pcc.privacycrashcam.applicationlogic;

import android.content.Context;

import edu.kit.informatik.pcc.android.Client;

/**
 * @author Giorgio Gross
 */

public class LogInHelper {

    /**
     * Checks whether the user logged in previously.
     * @param context application context
     * @return true is log in data was found, false otherwise
     */
    public static boolean IsUserLoggedIn(Context context) {
        return Client.getGlobal().getSessionManager().loadSessionToken() != null;
    }
}

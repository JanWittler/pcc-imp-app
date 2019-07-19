package de.pcc.privacycrashcam.applicationlogic;

import android.content.Context;

import de.pcc.privacycrashcam.data.Account;
import de.pcc.privacycrashcam.data.memoryaccess.MemoryManager;
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
        return Client.getGlobal().getSessionManager().loadAuthenticationToken() != null;
    }
}

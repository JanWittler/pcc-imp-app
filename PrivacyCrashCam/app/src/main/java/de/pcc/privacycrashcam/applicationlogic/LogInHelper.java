package de.pcc.privacycrashcam.applicationlogic;

import android.content.Context;

import de.pcc.privacycrashcam.data.Account;
import de.pcc.privacycrashcam.data.memoryaccess.MemoryManager;

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
        return true; // // TODO: 29.01.17
        // return new MemoryManager(context).getAccountData() != null;
    }

    /**
     * Saves the user account data
     *
     * @param mail user mail
     * @param pw user password
     * @param context application context
     */
    public static void SaveAccountData(String mail, String pw, Context context) {
        new MemoryManager(context).saveAccountData(new Account(mail, pw));
    }

}

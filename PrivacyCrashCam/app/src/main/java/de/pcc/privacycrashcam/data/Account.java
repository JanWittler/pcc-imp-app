package de.pcc.privacycrashcam.data;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Datacontainer for data concerning user accounts. Used to bundle the necessary information.
 *
 * @author David Laubenstein, Josh Romanowski
 */

public class Account {

    /**
     * Tag used for logging.
     */
    private final static String TAG = Account.class.getName();

    public final static String ACCOUNT_MAIN_KEY = "ACCOUNT";
    // JSON keys
    public final static String JSON_KEY_MAIL = "mail";
    public final static String JSON_KEY_PASSWORD = "password";

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    /**
     * E-mail address of the user
     */
    private String mail;
    /**
     * Password of the user.
     */
    private String password;

    /* #############################################################################################
     *                                  constructors
     * ###########################################################################################*/

    /**
     * constructor, which creates an account object with mail and password
     * @param mail represents the account mail
     * @param password represents the account password
     */
    public Account(String mail, String password) {
        this.mail = mail;
        this.password = password;
    }

    /**
     * constructor, which excludes mail and password from json and
     * assign them to the class variables
     * @param json is a JSON String, with all information inside
     */
    public Account(String json) throws JSONException {
        JSONObject account = new JSONObject(json);

        // retrieve json data
        this.mail = account.getString(JSON_KEY_MAIL);
        this.password = account.getString(JSON_KEY_PASSWORD);

    }

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    /**
     * Creates a JSON string out of the mail and password of the user.
     *
     * @return JSON String with account information inside
     */
    public String getAsJSON() {
        try {
            JSONObject json = new JSONObject("{}");
            json.put(JSON_KEY_MAIL, this.mail);
            json.put(JSON_KEY_PASSWORD, this.password);
            return json.toString();
        } catch (JSONException e) {
            Log.w(TAG, "Error creating account json");
        }
        return null;
    }

    /* #############################################################################################
     *                                  getter/ setter
     * ###########################################################################################*/

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}

package de.pcc.privacycrashcam.data;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author David Laubenstein, Josh Romanowski
 * Created by David Laubenstein on 1/24/17.
 */

public class Account {
    private final static String TAG = Account.class.getName();

    private final static String JSON_TAG_NAME = "name";
    private final static String JSON_TAG_PASSWORD = "password";

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    private String mail;
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
        this.mail = account.getString("mail");
        this.password = account.getString("password");

    }

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/
    /**
     * @return JSON String with account information inside
     */
    public String getAsJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put(JSON_TAG_NAME, this.mail);
            json.put(JSON_TAG_PASSWORD, this.password);
        } catch (JSONException e) {
            Log.w(TAG, "Error creating account json");
        }
        return json.toString();
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

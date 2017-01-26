package de.pcc.privacycrashcam.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author David Laubenstein
 * Created by David Laubenstein on 1/24/17.
 */

public class Account {
    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/
    String mail;
    String password;

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
    public Account(String json) {
        // in case, the account infos are in "account": {}
        JSONObject obj = null;
        JSONObject account = null;
        try {
            obj = new JSONObject(json);
            // go into account object
            account = obj.getJSONObject("account");
            // save Strings in account object to class attributes
            this.mail = account.getString("mail");
            this.password = account.getString("password");
        } catch (JSONException e) {
            // TODO: android exception handling???
            e.printStackTrace();
        }
    }

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/
    /**
     * @return JSON String with account information inside
     */
    public String getAsJSON() {
        //language=JSON
        String json = "{\n" +
                "  \"account\": {\n" +
                "    \"mail\": \"" + this.mail + "\",\n" +
                "    \"password\": \"" + this.password +"\"\n" +
                "  }\n" +
                "}";
        return json;
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

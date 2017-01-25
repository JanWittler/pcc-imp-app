package de.pcc.privacycrashcam.data;

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
        //TODO: convert json into mail and password
    }

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/
    /**
     * @return JSON String with account information inside
     */
    public String getAsJSON() {
        //TODO: return as json
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

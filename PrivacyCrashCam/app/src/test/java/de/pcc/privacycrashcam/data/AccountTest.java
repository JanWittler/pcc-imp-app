package de.pcc.privacycrashcam.data;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Giorgio Gross
 */
@Ignore // ignore these tests as we cannot test static classes. Maybe we find a solution later
public class AccountTest {
    private Account mAccount;
    private String mail = "mail";
    private String pw = "pw";

    @Before
    public void setUp() throws Exception {
        mAccount = new Account(mail, pw);
    }

    @Test
    public void getAsJSON() throws Exception {
        JSONObject jAccount = new JSONObject(mAccount.getAsJSON());

        assertEquals(mail, jAccount.getString(mail));
        assertEquals(pw, jAccount.getString(pw));
        assertEquals(2, jAccount.length());
    }

    @Test
    public void createFromJson() throws Exception {
        String sAccount = "{" +
                Account.JSON_KEY_MAIL + ":" + mail + "," +
                Account.JSON_KEY_PASSWORD  + ":" + pw + "," +
                "}";
        mAccount = new Account(sAccount);

        assertEquals(mail, mAccount.getMail());
        assertEquals(pw, mAccount.getPassword());
    }

}
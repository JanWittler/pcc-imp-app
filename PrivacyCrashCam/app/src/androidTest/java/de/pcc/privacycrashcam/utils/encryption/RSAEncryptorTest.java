package de.pcc.privacycrashcam.utils.encryption;

import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.crypto.SecretKey;

import de.pcc.privacycrashcam.BaseTest;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Testes the RSAEncryptor test
 *
 * @author Josh Romanowski, Giorgio Gross
 */
public class RSAEncryptorTest extends BaseTest {
    private RSAEncryptor encryptor;
    private AESEncryptor keyGen;
    private SecretKey secretKey;

    @Before
    public void setUp() {
        encryptor = new RSAEncryptor();
        keyGen = new AESEncryptor();

        secretKey = keyGen.generateKey();
    }

    @Test
    public void nullTest() {
        assertFalse(encryptor.encrypt(null, null, null));
    }

    @Test
    public void validTest() {
        InputStream publicKeyStream = null;
        try {
            publicKeyStream = new FileInputStream(encKey);
        } catch (FileNotFoundException e) {
            fail();
        }
        assertTrue(encryptor.encrypt(secretKey, publicKeyStream, encOutputTest));
        assertTrue(encOutputTest.exists());

        if (encOutputTest.exists())
            encOutputTest.delete();
    }
}
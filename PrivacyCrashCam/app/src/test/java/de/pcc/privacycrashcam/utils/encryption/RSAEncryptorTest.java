package de.pcc.privacycrashcam.utils.encryption;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.crypto.SecretKey;

import de.pcc.privacycrashcam.R;

import static junit.framework.Assert.*;

/**
 * Created by Josh Romanowski on 27.01.2017.
 */
public class RSAEncryptorTest {

    private static final String PUBLIC_KEY = "publickey.key";

    private RSAEncryptor encryptor;
    private AESEncryptor keyGen;
    private SecretKey secretKey;
    private File publicKey = null;

    @Before
    public void setUp() {
        encryptor = new RSAEncryptor();
        keyGen = new AESEncryptor();

        secretKey = keyGen.generateKey();

        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(PUBLIC_KEY);
        publicKey = new File(resource.getPath());
    }

    @Test
    public void nullTest() {
        assertFalse(encryptor.encrypt(null, null, null));
    }

    @Ignore
    @Test
    public void validTest() {
        InputStream publicKeyStream = null;
        try {
            publicKeyStream = new FileInputStream(publicKey);
        } catch (FileNotFoundException e) {
            fail();
        }
        File output = new File("output.txt");
        assertTrue(encryptor.encrypt(secretKey, publicKeyStream, output));
        assertTrue(output.exists());

        if (output.exists())
            output.delete();
    }
}
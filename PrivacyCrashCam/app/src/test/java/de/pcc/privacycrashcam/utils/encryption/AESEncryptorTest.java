package de.pcc.privacycrashcam.utils.encryption;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import javax.crypto.SecretKey;

import static junit.framework.Assert.*;

/**
 * Created by Josh Romanowski on 27.01.2017.
 */
public class AESEncryptorTest {

    private static final String VIDEO = "input.mp4";

    private AESEncryptor encryptor;
    private File video;

    @Before
    public void setUp() {
        encryptor = new AESEncryptor();

        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(VIDEO);
        video = new File(resource.getPath());
    }

    @Test
    public void testNull() {
        assertFalse(encryptor.encrypt(null, null, null));
    }

    @Test
    public void generateKeyTest() {
        SecretKey key = encryptor.generateKey();
        assertNotNull(key);
    }

    @Test
    public void keyAlgorithmTest() {
        SecretKey key = encryptor.generateKey();
        assertEquals(key.getAlgorithm(), "AES");
    }

    @Test
    public void encryptFileTest() {
        File output = new File("output.mp4");
        SecretKey key = encryptor.generateKey();
        assertTrue(encryptor.encrypt(video, key, output));
        assertTrue(output.exists());

        if (output.exists())
            output.delete();
    }
}
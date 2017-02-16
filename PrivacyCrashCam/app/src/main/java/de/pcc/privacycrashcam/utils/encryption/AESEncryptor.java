package de.pcc.privacycrashcam.utils.encryption;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Class that takes a symmetric AES SecretKey and encrypts files with it.
 *
 * @author Josh Romanowski
 */

public class AESEncryptor implements IFileEncryptor {

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    /**
     * Tag used for logging.
     */
    private static final String TAG = AESEncryptor.class.getName();

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    @Override
    public boolean encrypt(File input, SecretKey key, File output) {
        if (input == null || key == null || output == null) {
            return false;
        }

        // open files
        FileInputStream fis;
        FileOutputStream fos;
        try {
            fis = new FileInputStream(input);
            fos = new FileOutputStream(output);
        } catch (FileNotFoundException e) {
            Log.w(TAG, "Could not open or create files for encryption");
            return false;
        }

        // open cipher
        Cipher encipher;
        try {
            encipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            encipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            Log.w(TAG, "Initializing cipher failed");
            return false;
        }

        // write crypt
        CipherInputStream cis = new CipherInputStream(fis, encipher);
        try {
            int read;
            byte[] buffer = new byte[1024];
            while ((read = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
                fos.flush();
            }
            fos.close();
            cis.close();
            fis.close();
        } catch (IOException e) {
            Log.w(TAG, "Encrypting file failed");
            return false;
        }
        return true;
    }

    @Override
    public SecretKey generateKey() {
        KeyGenerator kgen;
        try {
            kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
        } catch (NoSuchAlgorithmException e) {
            Log.w(TAG, "Wasn't able to find AES algorithm");
            return null;
        }

        return kgen.generateKey();
    }
}

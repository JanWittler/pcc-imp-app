package de.pcc.privacycrashcam.utils.encryption;

import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Class that takes an asymmetric RSA key and encrypts symmetric keys with it.
 *
 * @author Josh Romanowski
 */

public class RSAEncryptor implements IKeyEncryptor {

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    /**
     * Tag used for logging.
     */
    private static final String TAG = RSAEncryptor.class.getName();

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    @Override
    public boolean encrypt(SecretKey input, InputStream publicKey, File output) {
        if (input == null || publicKey == null || output == null) {
            return false;
        }

        // encode key as string
        String encodedKey = Base64.encodeToString(input.getEncoded(), Base64.DEFAULT);

        ObjectInputStream inputStream;
        PublicKey key = null;

        // read public key
        try {
            inputStream = new ObjectInputStream(publicKey);
            key = (PublicKey) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error, reading key failed.");
        }

        // encrypt the string using the public key
        final byte[] cipherText = encryptData(encodedKey, key);
        if (cipherText == null) {
            return false;
        }

        // write the encrypted key
        try {
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(cipherText);
            fos.close();
        } catch (IOException e) {
            Log.w(TAG, "Writing encrypted symmetric key file failed");
            return false;
        }
        return true;
    }

     /* #############################################################################################
     *                                  helper methods
     * ###########################################################################################*/

    /**
     * Encrypts a text with an asymmetric public key.
     *
     * @param text Text to be encrypted.
     * @param key  Asymmetric RSA public key.
     * @return Returns the raw encrypted data.
     */
    private byte[] encryptData(String text, PublicKey key) {
        byte[] cipherText;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA");
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            Log.w(TAG, "Encrypting the symmetric key failed");
            return null;
        }
        return cipherText;
    }
}

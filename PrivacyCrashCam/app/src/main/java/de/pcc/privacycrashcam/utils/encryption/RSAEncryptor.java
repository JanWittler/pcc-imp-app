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
 * <p>
 * Specifically uses RSA/ECB/PKCS5Padding version of the RSA algorithm as standard RSA algorithms
 * vary between Android and desktop JRE's. Therefore be careful when changing the algorithms.
 * </p>
 *
 * @author Josh Romanowski
 */

class RSAEncryptor implements IKeyEncryptor {

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
            //Log.w(TAG, "Empty inputs");
            return false;
        }

        // encode key as string
        String encodedKey = Base64.encodeToString(input.getEncoded(), Base64.NO_WRAP);

        // read public key
        ObjectInputStream inputStream;
        PublicKey key;
        try {
            inputStream = new ObjectInputStream(publicKey);
            key = (PublicKey) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            //Log.w(TAG, "Reading public key failed");
            return false;
        }

        // encrypt the string using the public key
        byte[] cipherText;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(encodedKey.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            //Log.w(TAG, "Encrypting the symmetric key failed");
            return false;
        }

        // write the encrypted key
        try {
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(cipherText);
            fos.close();
        } catch (IOException e) {
            //Log.w(TAG, "Writing encrypted symmetric key file failed");
            return false;
        }
        return true;
    }
}

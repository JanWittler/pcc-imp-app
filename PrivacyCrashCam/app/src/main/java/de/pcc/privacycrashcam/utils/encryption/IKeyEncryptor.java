package de.pcc.privacycrashcam.utils.encryption;

import java.io.File;
import java.io.InputStream;

import javax.crypto.SecretKey;

/**
 * Interface for classes that encrypt symmetric secret keys with an asymmetric public key.
 * The active encryptor should be synced with the one on the web service.
 *
 * @author Josh Romanowski
 */

public interface IKeyEncryptor {

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    /**
     * Encrypts the given symmetric SecretKey with an asymmetric algorithm.
     * Therefore uses a public asymmetric key given as an input stream.
     * The key gets encoded via Base64.
     * After encrypting the symmetric key it gets saved to an output file.
     *
     * @param input     Symmetric SecretKey which should be encrypted.
     * @param publicKey Public key resource located in the app.
     * @param output    Output file for the encrypted symmetric key.
     * @return Returns whether encrypting the secret key was successful or not.
     */
    public boolean encrypt(SecretKey input, InputStream publicKey, File output);
}

package de.pcc.privacycrashcam.utils.encryption;

import java.io.File;

import javax.crypto.SecretKey;

/**
 * Interface for classes that encrypt files with a symmetric SecretKey.
 * The active encryptor should be synced with the one on the web service.
 *
 * @author Josh Romanowski
 */
interface IFileEncryptor {

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    /**
     * Encrypts a file input with a given symmetric key and writes the created crypt
     * into the output file.
     * The encryption is still fast for large inputs.
     *
     * @param input  File which should be encrypted.
     * @param key    Symmetric key used for encryption.
     * @param output Output file for the created crypt.
     * @return Returns whether encrypting the file was successful or not.
     */
    boolean encrypt(File input, SecretKey key, File output);

    /**
     * Creates a new symmetric SecretKey. The created key matches the used
     * algorithm e.g. AES.
     * The key can't be be used for other algorithms.
     *
     * @return Returns a newly generated symmetric key.
     */
    SecretKey generateKey();
}

package de.pcc.privacycrashcam.utils.encryption;

import java.io.File;
import java.io.InputStream;

import javax.crypto.SecretKey;

/**
 * The Encryptor is a Class used to encrypt files with a hybrid encryption algorithm.
 * It takes any amount of input files and encrypts them with a AES symmetric key.
 * Then it encrypts the key itself with a public RSA key.
 *
 * @author Josh Romanowski
 */

public class Encryptor {

    /* #############################################################################################
     *                                  attributes
     * ###########################################################################################*/

    /**
     * Encryptor used for encrypting files
     */
    private IFileEncryptor fileEncryptor;
    /**
     * Encryptor used for encrypting keys.
     */
    private IKeyEncryptor keyEncryptor;

    /* #############################################################################################
     *                                  constructors
     * ###########################################################################################*/

    /**
     * Creates a new hybrid encryptor which uses the AES algorithm for symmetric
     * and RSA for asymmetric encryption.
     */
    public Encryptor() {
        fileEncryptor = new AESEncryptor();
        keyEncryptor = new RSAEncryptor();
    }

    /* #############################################################################################
     *                                  methods
     * ###########################################################################################*/

    /**
     * Encrypts a number of input files via a hybrid encryption algorithm.
     * For each input file in the input file array an output file which is declared in
     * the output file array gets created.
     * Each entry in the input array gets mapped to the entry in the output array with the same index.
     * E.g. input[0] maps to output [0].
     * Therefore input and output have to be equally sized.
     *
     * @param input     Input array of all files to be anonymized.
     * @param output    Output array of the accompanying output files.
     * @param publicKey InputStream of the public asymmetric key file.
     * @param encKey    Output file for the encrypted symmetric key.
     * @return Returns whether encrypting the files and the symmetric key was successfull or not.
     */
    public boolean encrypt(File[] input, File[] output, InputStream publicKey, File encKey) {
        // assert inputs
        if (input == null || output == null || encKey == null) {
            return false;
        }

        if (input.length != output.length) {
            return false;
        }

        // create symmetric key
        SecretKey key = fileEncryptor.generateKey();
        if (key == null) {
            return false;
        }

        // encrypt
        for (int i = 0; i < input.length; i++) {
            if (!fileEncryptor.encrypt(input[i], key, output[i])) {
                return false;
            }
        }

        return keyEncryptor.encrypt(key, publicKey, encKey);
    }
}

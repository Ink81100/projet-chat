package com.projetchat;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Clase permettant de gérer le cryptage et décryptage de texte
 */
public final class CryptoHandler {
    /**
     * Permet de crypté un message via une clef secrète
     * 
     * @param message le message à crypté
     * @param key     la clef secrète
     * @return les octets du message crypté
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static final byte[] crypte(String message, SecretKey key) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // Initialisation
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, key);

        // Cryptage
        byte[] bytes = cipher.doFinal(message.getBytes());

        return bytes;
    }

    /**
     * Permet de décrypté un message via une clef secrète.
     * @param bytes le message crypté.
     * @param key la clef secrète.
     * @return Le message décrypté.
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static final String decrypte(byte[] bytes, SecretKey key) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // Initialisation
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key);

        // Décryptage
        byte[] message = cipher.doFinal(bytes);

        return new String(message);
    }
}

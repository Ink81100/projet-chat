package com.projetchat;

import javax.crypto.*;
import java.security.*;

/**
 * Clase permettant de gérer le cryptage/décryptage/signature de texte
 */
public final class CryptoHandler {
    /**
     * Permet de crypter un message via une clé secrète.
     *
     * @param message le message à crypter
     * @param key     la clé secrète
     * @return les octets du message crypté
     * @throws NoSuchAlgorithmException si l'algorithme de cryptage n'est pas disponible
     * @throws NoSuchPaddingException   si le schéma de remplissage n'est pas disponible
     * @throws InvalidKeyException      si la clé est invalide
     * @throws IllegalBlockSizeException si le bloc de données est de taille incorrecte
     * @throws BadPaddingException      si le remplissage est incorrect
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
     * @throws NoSuchAlgorithmException si l'algorithme de cryptage n'est pas disponible
     * @throws NoSuchPaddingException   si le schéma de remplissage n'est pas disponible
     * @throws InvalidKeyException      si la clé est invalide
     * @throws IllegalBlockSizeException si le bloc de données est de taille incorrecte
     * @throws BadPaddingException      si le remplissage est incorrect
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

    /**
     * Vérifie la signature de données
     * @param data les données
     * @param signature la signature des données
     * @param publicKey la clef publique utiliser (Une clef RSA)
     * @return {@code true} si la signature est bonne, sinon {@code false}
     * @throws InvalidKeyException      si la clé fournie est invalide
     * @throws SignatureException       si une erreur survient lors de la signature ou la vérification
     * @throws NoSuchAlgorithmException si l'algorithme de signature n'est pas disponible
     */
    public static final boolean verifSign(byte[] data, byte[] signature, PublicKey publicKey) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        Signature verifier = Signature.getInstance("SHA256withRSA");
        verifier.initVerify(publicKey);
        verifier.update(data);
        return verifier.verify(signature);
    }
}

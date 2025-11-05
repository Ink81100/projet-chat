package com.projetchat.server;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Classe permetant de générer differente type de clef (AES, RSA)
 */
final class GenereClef {
    /**
     * Générateur de clef AES
     * 
     * @return La clef symétrique AES
     * @throws NoSuchAlgorithmException
     */
    public static final SecretKey AESKeyGenerator() throws NoSuchAlgorithmException {
        KeyGenerator AESGenerator = KeyGenerator.getInstance("AES");
        return AESGenerator.generateKey();
    }

    /**
     * Générateur de clef AES
     * 
     * @return La paire de clef RSA (privée et publique)
     * @throws NoSuchAlgorithmException
     */
    public static final KeyPair RSAKeyGenerator() throws NoSuchAlgorithmException {
        KeyPairGenerator RSAGenerator = KeyPairGenerator.getInstance("RSA");
        return RSAGenerator.generateKeyPair();
    }
}

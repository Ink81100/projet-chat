package com.projetchat.server;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Classe permetant de générer differente type de clef (AES, RSA)
 */
public final class GenereClef {
    /**
     * Générateur de clef AES
     * 
     * @return La clef symétrique AES
     * @throws NoSuchAlgorithmException
     */
    public static final SecretKey AESKeyGenerator() throws NoSuchAlgorithmException {
        //Initialisation
        KeyGenerator AESGenerator = KeyGenerator.getInstance("AES");
        AESGenerator.init(256);

        return AESGenerator.generateKey();
    }

    /**
     * Générateur de clef RES
     * 
     * @return La paire de clef RSA (privée et publique)
     * @throws NoSuchAlgorithmException
     */
    public static final KeyPair RSAKeyGenerator() throws NoSuchAlgorithmException {
        //Initialisation
        KeyPairGenerator RSAGenerator = KeyPairGenerator.getInstance("RSA");
        RSAGenerator.initialize(256);
        
        return RSAGenerator.generateKeyPair();
    }
}

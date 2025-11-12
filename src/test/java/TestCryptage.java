import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

import com.projetchat.CryptoHandler;

public class TestCryptage {
    /**
     * Teste test le cryptage d'un message
     * 
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     */
    @Test
    void testCryptage() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        // Initialisation
        String message = "Messege de test";
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecretKey key = keyGenerator.generateKey();

        // Cryptage
        byte[] messageCrypte = CryptoHandler.crypte(message, key);

        // assert
        assertNotEquals(message, messageCrypte);
    }

    /**
     * Teste test le cryptage d'un message
     * 
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     */
    @Test
    void testDeCryptage() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        // Initialisation
        String message = "Messege de test";
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecretKey key = keyGenerator.generateKey();

        // Cryptage
        byte[] messageCrypte = CryptoHandler.crypte(message, key);
        // Décryptage
        String messageDecrypte = CryptoHandler.decrypte(messageCrypte, key);

        // assert
        assertEquals(message, messageDecrypte);
    }

    @Test 
    void testSignature() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.genKeyPair();

        String message = "Ceci est un message de test";

        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initSign(keyPair.getPrivate());
        signer.update(message.getBytes());

        assertTrue(CryptoHandler.verifSign(message.getBytes(), signer.sign(), keyPair.getPublic()));
    }

    @Test 
    void testMauvaiseSignature() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.genKeyPair();

        String message = "Ceci est un message de test";

        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initSign(keyPair.getPrivate());
        signer.update(message.getBytes());

        //Changement du message
        message = "Ceci est un nouveau message de test";

        assertFalse(CryptoHandler.verifSign(message.getBytes(), signer.sign(), keyPair.getPublic()));
    }
}

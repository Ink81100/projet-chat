import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

import com.projetchat.CryptoHandler;
import com.projetchat.server.GenereClef;

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
        SecretKey key = GenereClef.AESKeyGenerator();

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
        SecretKey key = GenereClef.AESKeyGenerator();

        // Cryptage
        byte[] messageCrypte = CryptoHandler.crypte(message, key);
        // Décryptage
        String messageDecrypte = CryptoHandler.decrypte(messageCrypte, key);

        // assert
        assertEquals(message, messageDecrypte);
    }
}

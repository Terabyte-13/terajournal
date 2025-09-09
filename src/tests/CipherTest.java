
package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import application.Cipher;



class CipherTest {
	
	Cipher cipher = new Cipher();
	
    @Test
    void testEncryptDecrypt() {
        String key = "aeiou";
        String original = "hello world";
        
        // Controllo che la stringa cifrata non sia rimasta uguale
        String encrypted = cipher.cipher(key, original, 0);
        assertNotEquals(original, encrypted);

        // Controllo se, decifrando la stringa, riottengo la stringa originale
        String decrypted = cipher.cipher(key, encrypted, 1);
        assertEquals(original, decrypted);
    }
    
    @Test
    void testDifferentInputsDifferentCiphertexts() {
        String key = "asdf123";
        String text1 = "gatto";
        String text2 = "palla";
        
        String encrypted1 = cipher.cipher(key, text1, 0);
        String encrypted2 = cipher.cipher(key, text2, 0);
        
        //controllo che stringhe diverse non producano la stessa frase cifrata
        assertNotEquals(encrypted1, encrypted2);
    }
}


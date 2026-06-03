package com.tera13.application.backend.file;

import com.tera13.application.backend.DiaryFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CipherTest {

    Cipher c;

    @BeforeEach
    void setUp(){
        c = new Cipher();
    }

    @Test
    void cipherTest() {
        String s = "String 123";
        String sEncrypted = c.cipher("key123", "String 123", 0);
        String sDecrypted = c.cipher("key123", sEncrypted, 1);

        assertEquals(s, sDecrypted);
    }
}
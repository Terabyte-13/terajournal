package com.tera13.application.backend;

import com.tera13.application.backend.file.FileFacade;
import com.tera13.application.bean.FileBean;
import com.tera13.application.exception.FileFacadeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class DiaryFacadeTest {

    private DiaryFacade df;
    Logger logger = Logger.getLogger("DiaryFacadeTest");

    @BeforeEach
    void setUp(){
         df = new DiaryFacade();
    }

    @Test //controlla che i dati decifrati e caricati siano uguali ai dati cifrati e salvati
    void saveAndLoadEncryptedPage() {

        String data = "test 123";
        String data2 = "";

        FileBean fb = new FileBean();
        fb.setData(data);
        fb.setKey("123abc");
        fb.setPath("/home/giacomo-framework/Documenti/terajournal/test.txt");

        try {
            df.savePage(fb);
            data2 = df.loadPageBean(fb).getData();
        } catch (FileFacadeException e) {
            logger.log(Level.SEVERE, "Eccezione in DiaryFacadeTest");
            e.printStackTrace();
        }

        assertEquals(data, data2);

    }

}
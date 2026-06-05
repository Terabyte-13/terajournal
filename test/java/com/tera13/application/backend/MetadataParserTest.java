package com.tera13.application.backend;

import com.tera13.application.backend.file.FileFacade;
import com.tera13.application.bean.FileBean;
import com.tera13.application.exception.FileFacadeException;
import com.tera13.application.exception.MetadataParserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class MetadataParserTest {

    private MetadataParser mp;
    private FileFacade ff;
    Logger logger = Logger.getLogger("MetadataParserTest");

    @TempDir //cartella temporanea per i test
    Path tempDir;

    @BeforeEach
    void setUp(){
        mp = new MetadataParser();
        ff = new FileFacade();
        mp.setFF(ff);
    }

    @Test
    void saveAndLoadField() {
        Path tempFile = tempDir.resolve("test_metadata.jm");
        String path = tempFile.toAbsolutePath().toString();
        String s = "a";
        String s2 = "b";


        try { //creo file
            ff.encryptAndSave(path, "", "", false);
        } catch (FileFacadeException e) {
            logger.log(Level.SEVERE, "Eccezione in MetadataParserTest");
            e.printStackTrace();
        }

        try {
            s = "Marco";
            mp.setField("Nome", path, s);
            s2 = mp.getField("Nome", path);
        } catch (MetadataParserException e) {
            logger.log(Level.SEVERE, "Eccezione in MetadataParserTest");
            e.printStackTrace();
        }

        assertEquals(s, s2);

    }

}
package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import application.Cipher;
import application.FileBean;
import application.FileFacade;
import application.FileManagerReal;

class FileManagerTest {


    @Test
    void testEncryptAndSaveThenLoadAndDecrypt(@TempDir Path tempDir) throws Exception {//IOexception nel caso di un eccezione del FileFacade, IllegalArgumentException nel caso di un errore nella sintassi dei dati del bean
        // Preparazione
        FileFacade fileFacade = new FileFacade();
        fileFacade.fm = new FileManagerReal();
        fileFacade.c = new Cipher();
    	
        String key = "aeiou";
        String originalData = "Hello World!";
        Path filePath = tempDir.resolve("test.html");

        FileBean beanToSave = new FileBean();
        beanToSave.setPath(filePath.toString());
        beanToSave.setKey(key);
        beanToSave.setData(originalData);

        // Salvo e cifro, controllo se è andato a buon fine
        int saveResult = fileFacade.encryptAndSaveBean(beanToSave, true, true);
        assertEquals(1, saveResult);
        assertTrue(Files.exists(filePath));

        // Verifico che il file non contenga i dati in chiaro
        String fileContent = Files.readString(filePath);
        assertNotEquals(originalData, fileContent);

        // Leggo e decifro, controllo se i dati decifrati combaciano con quelli salvati prima
        FileBean beanToLoad = new FileBean();
        beanToLoad.setPath(filePath.toString());
        beanToLoad.setKey(key);

        FileBean loaded = fileFacade.loadAndDecryptBean(beanToLoad, true);
        assertEquals(originalData, loaded.getData());
    }
}

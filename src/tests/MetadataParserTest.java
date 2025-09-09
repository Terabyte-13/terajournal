package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import application.FileFacade;
import application.MetadataBean;
import application.MetadataParser;

class MetadataParserTest {

	FileFacade ff;
	File testFile;
	MetadataParser mp;
	
    @BeforeEach
    void setUp() throws Exception {
        mp = new MetadataParser();
        mp.setFF(new FileFacade());
        
        ff = new FileFacade();
        testFile = new File(tempDir, "metadata.txt");

        // Scriviamo un contenuto iniziale
        String initialData = String.join("\n",
                "name:Diario",
                "pwdHash:123abc"
        );
        ff.encryptAndSave(testFile.getAbsolutePath(), initialData, null, true, false);
    }
    
    
    @Test
    void testGetFieldBean() { //Testo se viene restituito un field esistente
        MetadataBean mb = new MetadataBean();
        mb.setPath(testFile.getAbsolutePath());
        mb.setFieldName("name");

        MetadataBean result = mp.getFieldBean(mb);

        assertEquals("Diario", result.getFieldData());
    }
    
  //Testo se viene restituito notFound per un field non esistente

    @Test
    void testSetFieldBean() throws Exception { //Testo l'impostazione di un nuovo field
        MetadataBean mb = new MetadataBean();
        mb.setPath(testFile.getAbsolutePath());
        mb.setFieldName("version");
        mb.setFieldData("1.0");

        int result = mp.setFieldBean(mb);
        assertEquals(1, result);

        String fileContent = ff.loadAndDecrypt(testFile.getAbsolutePath(), null, false);
        assertTrue(fileContent.contains("version:1.0"));
    }
    
}

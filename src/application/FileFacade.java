package application;

import java.nio.file.Path;
import java.nio.file.Paths;

//TODO: usa FileManagerDemo se si è in modalità demo
public class FileFacade {
	FileManagerReal fm = new FileManagerReal();
	Cipher c = new Cipher();
	String key = null;
	
	int setKey(String newKey) {
		return 1;
	}

	int encryptAndSave(String data, String outputPath, Boolean confirmOverwrite) {
		String encryptedData;
		//devo dividere il path in directory e filename
		//lo trasformo in un oggetto Path per avere l'operazione getFileName
		String fileName = Paths.get(outputPath).getFileName().toString();
		String directory;
		if(Paths.get(outputPath).getParent() != null) {
			directory = Paths.get(outputPath).getParent().toString();
		}else {
			directory = "";
		}
		
		System.out.println(directory + "  " + fileName);
		
		//salvataggio file (o gestione se il file non è stato salvato)
		if(key != null) {
			encryptedData = c.cipher(key, data, 0);
			if(fm.save(encryptedData, directory, fileName, confirmOverwrite) == 1) {
				return 1;
			}else { return 0;}
		}else {
			System.out.println("<FileFacade> nessuna key! salvo il file senza cifrare");
			if(fm.save(data, directory, fileName, confirmOverwrite) == 1) {
				return 1;
			}else { return 0;}
			}
	}
	
	String loadAndDecrypt(String inputPath) {
		String decryptedData;
		
		if(key != null) {
			decryptedData = c.cipher(key, fm.load(inputPath), 1);
		}else {
			System.out.println("<FileFacade> nessuna key! carico il file senza decifrare");
			decryptedData = fm.load(inputPath);
		}
		
		return decryptedData;
	}
	
}


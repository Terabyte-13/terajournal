package application;

import java.io.File;
import java.nio.file.Paths;

public class FileFacade {
	Cipher c = new Cipher();
	FileManager fm;
	Boolean demoMode = false;

	FileFacade(){
		if(demoMode == true) {fm = new FileManagerDemo();}
		else{fm = new FileManagerReal();}
	}

	//salva su FS il file ricevuto come bean
	int encryptAndSaveBean(FileBean fileBean, Boolean confirmOverwrite, Boolean encrypt) {
		//Estrazione dati dal bean ----------
		String outputPath = fileBean.getPath();
		String data = fileBean.getData();
		String key = fileBean.getKey();
		//-----------------------------------
		return encryptAndSave(outputPath, data, key, confirmOverwrite, encrypt);
	}

	int encryptAndSave(String outputPath, String data, String key, Boolean confirmOverwrite, Boolean encrypt) {

		String encryptedData;
		
		//devo dividere il path in directory e filename
		//Trasformo la stringa in un oggetto Path per avere l'operazione getFileName
		
		//Ricavo il nome file
		String fileName = Paths.get(outputPath).getFileName().toString();

		//Ricavo la directory contenitrice
		String directory;
		if(Paths.get(outputPath).getParent() != null) {
			directory = Paths.get(outputPath).getParent().toString();
		}else {
			directory = "";
		}
		
		System.out.println(directory + "  " + fileName);
		
		//salvataggio e cifratura file (o gestione se il file non è stato salvato)
		if(key != null && encrypt == true) {
			encryptedData = c.cipher(key, data, 0);
			if(fm.save(encryptedData, directory, fileName, confirmOverwrite) == 1) {
				return 1;
			}else { return 0;}
		}else {
			System.out.println("<FileFacade> salvo il file senza cifrare");
			if(fm.save(data, directory, fileName, confirmOverwrite) == 1) {
				return 1;
			}else { return 0;}
			}
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	//carica i dati del file specificato dal bean sulla variabile Data del bean, e lo restituisce
	FileBean loadAndDecryptBean(FileBean fileBean, Boolean decrypt) {
		//Estrazione dati dal bean ----------
		String inputPath = fileBean.getPath();
		String key = fileBean.getKey();
		//-----------------------------------
		fileBean.setData(loadAndDecrypt(inputPath, key, decrypt)); 
		return fileBean;
	}
	
	String loadAndDecrypt(String inputPath, String key, Boolean decrypt) {
		String decryptedData;
		
		if(key != null && decrypt == true) {
			decryptedData = c.cipher(key, fm.load(inputPath), 1);
		}else {
			System.out.println("<FileFacade> carico il file senza decifrare");
			decryptedData = fm.load(inputPath);
		}
		
		return decryptedData;
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	//controlla se il file e' presente
	Boolean checkForFile(String path) {
		return fm.checkForFile(path);
	}
	
}


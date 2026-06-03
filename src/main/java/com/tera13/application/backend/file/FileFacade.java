package com.tera13.application.backend.file;

import com.tera13.application.exception.FileFacadeException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileFacade {
	Cipher c = new Cipher();
	FileDAO fm;
	Boolean demoMode = false;
	Logger logger = Logger.getLogger("FileFacade");

	public FileFacade(){
		if(Boolean.TRUE.equals(demoMode)) {fm = new FileDAODB();}
		else{fm = new FileDAOFS();}
	}

	public int encryptAndSave(String outputPath, String data, String key, Boolean confirmOverwrite, Boolean encrypt) throws FileFacadeException {

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
		
		
		//salvataggio e cifratura file (o gestione se il file non è stato salvato)
		try{
			if(key != null && encrypt) {
				encryptedData = c.cipher(key, data, 0);
				if(fm.save(encryptedData, directory, fileName, confirmOverwrite) == 1) {
					return 1;
				}else { return 0;}
			}else {
				logger.log(Level.INFO, "salvo il file senza cifrare");
				if(fm.save(data, directory, fileName, confirmOverwrite) == 1) {
					return 1;
				}else { return 0;}
			}
		}catch (IOException e){
			throw new FileFacadeException(e.getMessage(), e.getCause());
		}
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	public String loadAndDecrypt(String inputPath, String key, Boolean decrypt) throws FileFacadeException {
		String decryptedData;
		
		try{
			if(key != null && decrypt) {
				decryptedData = c.cipher(key, fm.load(inputPath), 1);
			}else {
				logger.log(Level.INFO, "carico il file senza decifrare");
				decryptedData = fm.load(inputPath);
			}
		}catch(IOException e){
			throw new FileFacadeException(e.getMessage(), e.getCause());
		}

		return decryptedData;
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	//controlla se il file e' presente
    public Boolean checkForFile(String path) {
		return fm.checkForFile(path);
	}
	
}


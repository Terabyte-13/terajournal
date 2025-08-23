package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class FileManagerReal extends FileManager {
	
	Alert fileExistsAlert = new Alert(AlertType.CONFIRMATION);
	ButtonType overwrite = new ButtonType("Sovrascrivi");
	ButtonType cancel = new ButtonType("Annulla");
	
	File outputFile;
	
	Logger logger = Logger.getLogger("Hasher");
	StringBuilder bui = new StringBuilder();

	int save(String data, String outputPath, String fileName, Boolean confirmOverwrite) {
		try {
			// creazione cartelle e file ------------------------------------------------------------------------
			if(!outputPath.equals("")) {
				outputFile = new File(outputPath); //directory
				outputFile.mkdirs();
				outputFile = new File(outputPath, fileName); //il file
			}else {
				outputFile = new File(fileName); //direttamente il file se non c'è directory contenente
			}
			//controllo se il file esiste già
			//			 vv  uso questo perchè restituisce false anche se OutputFile.exists() restituisse null.			
			if(Boolean.FALSE.equals(outputFile.exists()) || confirmOverwrite) {
				outputFile.createNewFile();
				logger.log(Level.INFO, "File creato: {0}", outputFile.getName());
				//return 1;
			} else { // popup file già esistente -------------------------------------------------------------- TODO appare sempre
				logger.log(Level.INFO, "File già esistente");
				fileExistsAlert.setTitle("File già esistente");
				fileExistsAlert.setHeaderText("Questo file è già presente. Sovrascrivere?");
				fileExistsAlert.setContentText(outputPath + File.separator + fileName);
				fileExistsAlert.getButtonTypes().setAll(overwrite,cancel);
				Optional<ButtonType> choice = fileExistsAlert.showAndWait();
				if(choice.isPresent() && choice.get() == cancel) {
					logger.log(Level.WARNING, "Creazione file annullata");
					return 0;
				}
					//return 1;
				}
		}catch (IOException e) {
			logger.log(Level.SEVERE, "Errore nella creazione del file {0}/{1}", new String[]{outputPath, fileName}); //va fatto cosi' se bisogna inserire più variabili in un log
			e.printStackTrace();
		}
		
		//creando il writer fra le parentesi del try, viene chiuso il writer automaticamente
		try(FileWriter writer = new FileWriter(outputFile)){
			// scrittura sul file ------------------------------------------------------------------------
			writer.write(data);
			logger.log(Level.INFO, "File scritto: {0}", outputFile.getName());
		}catch (IOException e){
			logger.log(Level.SEVERE, "Errore nella scrittura del file {0}/{1}", new String[]{outputPath, fileName});
			e.printStackTrace();
			return 0;
		}
		
		return 1;
	}
	
	String load(String inputPath) {
		File inputFile;
		Scanner reader;
		String data = "";
		
		try {
		// apertura del file
		inputFile = new File(inputPath);
		reader = new Scanner(inputFile);
		// lettura del file e scrittura sul buffer --------------
		bui.setLength(0); //resetto lo stringbuilder
		bui.append(data);
		while(reader.hasNextLine()) {
			bui.append(reader.nextLine());
			bui.append("\n");
		}
		data = bui.toString();
		reader.close();
		logger.log(Level.INFO, "File letto: {0}", inputFile.getName());
		// --------------
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "Errore nell apertura del file {0}", inputPath);
			e.printStackTrace();
			return null;
		}
		return data;
	}
	
	Boolean checkForFile(String path) {
		File file = new File(path);
		return file.exists();
	}
	
}

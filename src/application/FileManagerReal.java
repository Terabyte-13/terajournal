package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class FileManagerReal extends FileManager {
	
	Alert fileExistsAlert = new Alert(AlertType.CONFIRMATION);
	ButtonType overwrite = new ButtonType("Sovrascrivi");
	ButtonType cancel = new ButtonType("Annulla");
	
	File outputFile;

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
				System.out.printf("<FileMan> File creato: %s%n", outputFile.getName());
				//return 1;
			} else { // popup file già esistente -------------------------------------------------------------- TODO appare sempre
				System.out.println("<FileMan> File gia' esistente");
				fileExistsAlert.setTitle("File già esistente");
				fileExistsAlert.setHeaderText("Questo file è già presente. Sovrascrivere?");
				fileExistsAlert.setContentText(outputPath + File.separator + fileName);
				fileExistsAlert.getButtonTypes().setAll(overwrite,cancel);
				Optional<ButtonType> choice = fileExistsAlert.showAndWait();
				if(choice.isPresent() && choice.get() == cancel) {
					System.out.println("<FileMan> Creazione file annullata");
					return 0;
				}
					//return 1;
				}
		}catch (IOException e) {
			System.out.println("<FileMan> Errore nella creazione del file '" + outputPath + File.separator + fileName + "'");
			e.printStackTrace();
		}
		
		//creando il writer fra le parentesi del try, viene chiuso il writer automaticamente
		try(FileWriter writer = new FileWriter(outputFile)){
			// scrittura sul file ------------------------------------------------------------------------
			writer.write(data);
			System.out.printf("<FileMan> File scritto: %s%n", outputFile.getName());
			System.out.printf("[%s]\n", data);
		}catch (IOException e){
			System.out.println("<FileMan> Errore nella scrittura del file '" + outputPath + File.separator + fileName + "'");
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
		while(reader.hasNextLine()) {
			data = data + reader.nextLine() + "\n";
		}
		reader.close();
		System.out.printf("<FileMan> File letto: %s%n", inputFile.getName());
		// --------------
		} catch (FileNotFoundException e) {
			System.out.println("<FileMan> Errore nell'apertura del file " + inputPath);
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

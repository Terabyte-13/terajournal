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
				outputFile = new File(outputPath);
				outputFile.mkdirs();
				outputFile = new File(outputPath, fileName);
			}else {
				outputFile = new File(fileName);
			}
//         				 vv restituisce false anche se per qualche motivo OutputFile.exists() restituisce null.			
			if(Boolean.FALSE.equals(outputFile.exists()) || confirmOverwrite) {
				outputFile.createNewFile();
				System.out.printf("<FileMan> File creato: %s\n", outputFile.getName());
			} else { // popup file già esistente --------------------------------------------------------------
				System.out.println("<FileMan> File gia' esistente");
				fileExistsAlert.setTitle("File già esistente");
				fileExistsAlert.setHeaderText("Questo file è già presente. Sovrascrivere?");
				fileExistsAlert.setContentText(outputPath + "/" + fileName);
				fileExistsAlert.getButtonTypes().setAll(overwrite,cancel);
				Optional<ButtonType> choice = fileExistsAlert.showAndWait();

				if(choice.isPresent() && choice.get() == cancel) {
					System.out.println("<FileMan> Creazione file annullata");
					return 0;
				}
				}
		}catch (IOException e) {
			System.out.println("<FileMan> Errore nella creazione del file");
			e.printStackTrace();
			return 0;
		}
		
		//creando il writer fra le parentesi del try, viene chiuso il writer anche in caso di eccezione
		try(FileWriter writer = new FileWriter(outputFile)){
			// scrittura sul file ------------------------------------------------------------------------
			writer.write(data);
			writer.close();
			System.out.printf("<FileMan> File scritto: %s\n", outputFile.getName());
		}catch (IOException e){
			System.out.println("<FileMan> Errore nella creazione del file");
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
			System.out.println("<FileMan> Errore nell'apertura del file");
			e.printStackTrace();
			return null;
		}
		return data;
	}
	
}

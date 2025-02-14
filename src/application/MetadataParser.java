package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class MetadataParser {
	
	BufferedReader fr;
	StringTokenizer tok;
	FileFacade ff;
	
	int findField(String fieldName, String filePath) {
		String line = "";
		int i = 0;
		try {
			fr = new BufferedReader(new FileReader(filePath));
			
			line = fr.readLine(); //prendi la riga
			while(line != null) {
				tok = new StringTokenizer(line, ":");
				if(tok.hasMoreTokens()){ //estrai il primo token della riga
					line = tok.nextToken();
					}
				
				if(line.equals(fieldName) && tok.hasMoreTokens()) {
					return i;
				}
				i++;
				line = fr.readLine();
			}
			
		} catch (Exception e) {
			System.out.println("MetadataParser.findField: Errore nella lettura del file.");
			e.printStackTrace();
		}
	return -1;
	}
	
	String getField(String fieldName, String filePath) {
		String output = "";
		int i = findField(fieldName, filePath);
		if(i < 0) {
			System.out.printf("<MetadataParser.getField> field '%s' non trovata.%n", fieldName);
			return "notFound";
		}
		try {
			fr = new BufferedReader(new FileReader(filePath));
			
			for(int n = 0; n < i; n++) { //vado alla posizione trovata del field
				fr.readLine();
			}
			output = fr.readLine();
			tok = new StringTokenizer(output, ":");
			tok.nextToken(); //avanzo al secondo token
			return tok.nextToken();
			
		} catch (Exception e) {
			System.out.println("<MetadataParser.getField> Errore nella lettura del file.");
			e.printStackTrace();
		}
	return null;
	}
	
	String setField(String fieldName, String filePath, String newValue) {
		try {
			FileFacade ff = new FileFacade();
			String data = ff.loadAndDecrypt(filePath);
			List<String> lines = new ArrayList<String>(Arrays.asList(data.split("\n")));
			
			int i = findField(fieldName, filePath);
			if(i >= 0) {lines.set(i, fieldName + ":" + newValue);}
			if(i < 0) {lines.add(fieldName + ":" + newValue);}
			
			data = String.join("\n", lines);
			System.out.println(data);
			ff.encryptAndSave(data, filePath, true);
			
			
		} catch (Exception e) {
			System.out.println("<MetadataParser.setField> Errore nella scrittura del file.");
			e.printStackTrace();
		}
	return null;
	}
	
	
	List<String> getFieldNames(String filePath){
		try{
			List<String> output = fileToList(filePath);
			for(int i = 0; i < output.size(); i++) {
				System.out.println(output.get(i));
				tok = new StringTokenizer(output.get(i), ":");
				output.set(i, tok.nextToken()); //prendo il primo token
			}
			return output;
			} catch (Exception e) {
				System.out.println("<MetadataParser.getFields> Errore nella lettura del file.");
				e.printStackTrace();
				return null;
			}
	}
	

	List<String> fileToList(String filePath) {
		try{
			List<String> output;
			ff = new FileFacade();
			String data = ff.loadAndDecrypt(filePath);
			output = new ArrayList<String>(Arrays.asList(data.split("\n")));
			return output;
		} catch (Exception e) {
			System.out.println("<MetadataParser.fileToList> Errore nella scrittura del file.");
			e.printStackTrace();
			return null;
		}
	}
	
	int appendToFile(String filePath, String line) { //mi creo una funzione mia per farla interfacciare con FileFacade
		try{
			ff = new FileFacade();
			String data;
			List<String> lines = fileToList(filePath);
			lines.add(line);
			data = String.join("\n", lines);
			ff.encryptAndSave(filePath, data, true);
			return 1;
		} catch (Exception e) {
			System.out.println("<MetadataParser.appendToFile> Errore nella scrittura del file.");
			e.printStackTrace();
			return 0;
		}
	}
	
}

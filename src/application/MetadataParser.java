package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MetadataParser {
	
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_CYAN = "\\e[0;36m";
	public static final String ANSI_RESET = "\u001B[0m";
	
	BufferedReader fr;
	StringTokenizer tok;
	FileFacade ff;
	
	Logger logger = Logger.getLogger("MetadataParser");
	
	MetadataParser(){
		String caller = Thread.currentThread().getStackTrace()[2].getClassName();
		logger.log(Level.FINE, "Creato da {0}.", caller);
	}
	
	void setFF(FileFacade newff) {
		ff = newff;
		logger.log(Level.FINE, "FileFacade impostato: {0}", ff);
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	//restituisce il numero della riga in cui si trova il field specificato
	int findField(String fieldName, String filePath) {
		String line = "";
		int i = 0;
		try {
			String data = ff.loadAndDecrypt(filePath, null, false);
			if(data == null) {logger.log(Level.SEVERE, "{0}file vuoto!!{1}", new Object[] {ANSI_CYAN, ANSI_RESET});}
			fr = new BufferedReader(new StringReader(data));
			
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
			logger.log(Level.SEVERE, "{0}Errore nella lettura del file.{1}", new Object[] {ANSI_YELLOW, ANSI_RESET});
			e.printStackTrace();
		}
	return -1;
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	MetadataBean getFieldBean(MetadataBean mb) {
		//estrazione richiesta dal bean ---
		String filePath = mb.getPath();
		String fieldName = mb.getFieldName();
		//----------------------------------
		mb.setFieldData(getField(fieldName, filePath));
		return mb;
	}
	
	String getField(String fieldName, String filePath) {
		String output = "";
		
		int i = findField(fieldName, filePath);
		if(i < 0) {
			logger.log(Level.SEVERE, "{0}field {1} non trovata in {2}.{3}", new Object[] {ANSI_YELLOW, fieldName, filePath, ANSI_RESET});
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
			logger.log(Level.SEVERE, "{0}Errore in getField.{1}", new Object[] {ANSI_YELLOW, ANSI_RESET});
			e.printStackTrace();
		}
	return null;
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	int setFieldBean(MetadataBean mb) {
		//estrazione richiesta dal bean ---
		String filePath = mb.getPath();
		String fieldName = mb.getFieldName();
		String fieldData = mb.getFieldData();
		//----------------------------------
		return setField(fieldName, filePath, fieldData);
	}
	
	int setField(String fieldName, String filePath, String newValue) {
		try {
			String data = ff.loadAndDecrypt(filePath, null, false);
			List<String> lines = new ArrayList<>(Arrays.asList(data.split("\n")));
			
			int i = findField(fieldName, filePath);
			if(i >= 0) {lines.set(i, fieldName + ":" + newValue);}
			if(i < 0) {lines.add(fieldName + ":" + newValue);}
			
			data = String.join("\n", lines);
			ff.encryptAndSave(filePath, data, null, true, false);
			return 1;
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "{0}Errore nella scrittura del file.{1}", new Object[] {ANSI_YELLOW, ANSI_RESET});
			e.printStackTrace();
			return -1;
		}
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	List<String> getFieldNames(String filePath){
		try{
			List<String> output = fileToList(filePath);
			if(output == null) {return Collections.emptyList();}
			for(int i = 0; i < output.size(); i++) {
				tok = new StringTokenizer(output.get(i), ":");
				if(tok.hasMoreElements()) {output.set(i, tok.nextToken());} //prendo il primo token
			}
			return output;
			} catch (Exception e) {
				logger.log(Level.SEVERE, "{0}Errore nella lettura del file.{1}", new Object[] {ANSI_YELLOW, ANSI_RESET});
				e.printStackTrace();
				return Collections.emptyList();
			}
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	List<String> fileToList(String filePath) {
		try{
			List<String> output = null;
			
			String data = ff.loadAndDecrypt(filePath, null, false);
			if(data != null) {output = new ArrayList<>(Arrays.asList(data.split("\n")));}
			return output;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "{0}Errore nella scrittura del file.{1}", new Object[] {ANSI_YELLOW, ANSI_RESET});
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	int appendToFile(String filePath, String line) { //mi creo una funzione mia per farla interfacciare con FileFacade
		try{
			String data;
			List<String> lines = fileToList(filePath);
			lines.add(line);
			data = String.join("\n", lines);
			ff.encryptAndSave(filePath, data, null, true, false);
			return 1;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "{0}Errore nella scrittura del file.{1}", new Object[] {ANSI_YELLOW, ANSI_RESET});
			e.printStackTrace();
			return 0;
		}
	}
	
}

package application;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
Problemi:
- Si crea un delimitatore in più all'inizio della stringa (risolto in maniera brutta? o forse non causava nemmeno problemi)
- delimitando con : scavalca nella riga dopo e legge fino al : della riga dopo (risolto?)
- in modalità demo i file vengono salvati in notFound anche se il diario sembra sia creato correttamente (non trova la field "folder" la riga la legga correttamente)
*/

public class MetadataParser {
	
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_RESET = "\u001B[0m";
	
	private static final String WRITE_ERROR = "{0}Errore nella scrittura del file.{1}";
	private static final String READ_ERROR = "{0}Errore nella lettura del file.{1}";
	
	BufferedReader fr;
	StringTokenizer tok;
	FileFacade ff;

	String lineSeparator = "\n";
	String tokSeparator = ":";
	
	Logger logger = Logger.getLogger("MetadataParser");
	
	MetadataParser(){
		String caller = Thread.currentThread().getStackTrace()[2].getClassName();
		logger.log(Level.FINE, "Creato da {0}.", caller);
	}
	
	void setFF(FileFacade newff) {
		ff = newff;
		logger.log(Level.INFO, "FileFacade impostato: {0}", ff);
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	//restituisce il numero della riga in cui si trova il field specificato
	int findField(String fieldName, String filePath) {
		String line = "";
		int i = 0;
		try {
			String data = ff.loadAndDecrypt(filePath, null, false); //TODO beanizza
			logger.log(Level.INFO, "Leggo metadati: {0}path:{1} ## data:[{2}]{3}", new Object[] {ANSI_CYAN, filePath, data, ANSI_RESET});
			if(data == null) {
				logger.log(Level.SEVERE, "{0}file {1} vuoto!! (istanza FM: {2}){3}", new Object[] {ANSI_CYAN, filePath, ff.fm, ANSI_RESET});
			}
			fr = new BufferedReader(new StringReader(data));
			
			line = fr.readLine(); //prendi la riga
			while(line != null) {
				tok = new StringTokenizer(line, tokSeparator);
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
			logger.log(Level.SEVERE, READ_ERROR, new Object[] {ANSI_YELLOW, ANSI_RESET});
			e.printStackTrace();
		}
	logger.log(Level.SEVERE, "findField: field non trovata.", new Object[] {ANSI_YELLOW, ANSI_RESET});
	return -1;
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	MetadataBean getFieldBean(MetadataBean mb) {
		try {
			//estrazione richiesta dal bean ---
			String filePath = mb.getPath();
			String fieldName = mb.getFieldName();
			//----------------------------------
			mb.setFieldData(getField(fieldName, filePath));
			return mb;
		}catch(IllegalArgumentException e) {
			logger.log(Level.SEVERE, "Dati field invalidi.");
			e.printStackTrace();
			return mb;
		}
	}
	
	String getField(String fieldName, String filePath) {
		String output = "";
		
		int i = findField(fieldName, filePath);
		if(i < 0) {
			logger.log(Level.SEVERE, "{0}field {1} non trovata in {2}.{3}", new Object[] {ANSI_YELLOW, fieldName, filePath, ANSI_RESET});
			return "notFound";
		}
		try {
			//leggo il file tramite bean ------
			FileBean fb = new FileBean();
			fb.setPath(filePath);
			fb = ff.loadAndDecryptBean(fb, false);
			//--------------------------------
			fr = new BufferedReader(new StringReader(fb.getData()));
			
			for(int n = 0; n < i; n++) { //vado alla posizione trovata del field
				fr.readLine();
			}
			output = fr.readLine();
			logger.log(Level.INFO, "leggo riga metadati:{0}", output);
			tok = new StringTokenizer(output, tokSeparator);
			String t = tok.nextToken(); //avanzo al secondo token
			logger.log(Level.INFO, "leggo token:{0}", t);
			t = tok.nextToken(); //avanzo al secondo token
			logger.log(Level.INFO, "leggo token:{0}", t);
			return t;
			//tok.nextToken(); //avanzo al secondo token TODO leva la roba sopra e rimetti questo
			//return tok.nextToken();
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "{0}Errore in getField. fieldName:{1}; filePath:{2}; Dati: {3}{4}", new Object[] {ANSI_YELLOW, fieldName, filePath, ff.loadAndDecrypt(filePath, null, false), ANSI_RESET});
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
			List<String> lines = new ArrayList<>(Arrays.asList(data.split(lineSeparator))); //leggo il file metadati e divido i field ogni separator

			//se è presente, aggiorna il dato, altrimenti crea una nuova riga
			int i = findField(fieldName, filePath);
			if(i >= 0) {lines.set(i, fieldName + ":" + newValue);}
			else {lines.add(fieldName + ":" + newValue);}
			
			data = String.join(lineSeparator, lines);
			if(data.charAt(0) == lineSeparator.charAt(0)) {data = data.substring(1);}
			logger.log(Level.INFO, "dati setField:[{0}]", data);
			ff.encryptAndSave(filePath, data, null, true, false);
			return 1;
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, WRITE_ERROR, new Object[] {ANSI_YELLOW, ANSI_RESET});
			e.printStackTrace();
			return -1;
		}
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	List<String> getFieldNames(String filePath){
		try{
			List<String> output = fileToList(filePath);
			if(output.isEmpty()) {return Collections.emptyList();}
			for(int i = 0; i < output.size(); i++) {
				tok = new StringTokenizer(output.get(i), tokSeparator);
				if(tok.hasMoreElements()) {output.set(i, tok.nextToken());} //prendo il primo token
			}
			return output;
			} catch (Exception e) {
				logger.log(Level.SEVERE, READ_ERROR, new Object[] {ANSI_YELLOW, ANSI_RESET});
				e.printStackTrace();
				return Collections.emptyList();
			}
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	List<String> fileToList(String filePath) {
		try{
			List<String> output = null;
			String data = ff.loadAndDecrypt(filePath, null, false); //TODO beanizza
			if(data != null) {output = new ArrayList<>(Arrays.asList(data.split(lineSeparator)));}
			return output;
		} catch (Exception e) {
			logger.log(Level.SEVERE, WRITE_ERROR, new Object[] {ANSI_YELLOW, ANSI_RESET});
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
			data = String.join(lineSeparator, lines);
			ff.encryptAndSave(filePath, data, null, true, false);
			return 1;
		} catch (Exception e) {
			logger.log(Level.SEVERE, WRITE_ERROR, new Object[] {ANSI_YELLOW, ANSI_RESET});
			e.printStackTrace();
			return 0;
		}
	}
	
}

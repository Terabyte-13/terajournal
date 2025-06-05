package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class MetadataParser {
	
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_RESET = "\u001B[0m";
	
	BufferedReader fr;
	StringTokenizer tok;
	FileFacade ff;
	
	MetadataParser(){
		String caller = Thread.currentThread().getStackTrace()[2].getClassName();
		System.out.printf("<MetadataParser> aperto da %s.%n", caller);
	}
	
	void setFF(FileFacade newff) {
		ff = newff;
		System.out.printf("<MetadataParser> FF impostato: %s.%n", ff);
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	//restituisce il numero della riga in cui si trova il field specificato
	int findField(String fieldName, String filePath) {
		String line = "";
		int i = 0;
		try {
			String data = ff.loadAndDecrypt(filePath, null, false);
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
			System.out.println(ANSI_YELLOW +"<MetadataParser.findField>: Errore nella lettura del file."+ ANSI_RESET);
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
			System.out.printf("%s<MetadataParser.getField> field '%s' non trovata in %s.%n%s", ANSI_YELLOW, fieldName, filePath, ANSI_RESET);
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
			System.out.println(ANSI_YELLOW +"<MetadataParser.getField> Errore nella lettura del file." + ANSI_RESET);
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
			List<String> lines = new ArrayList<String>(Arrays.asList(data.split("\n")));
			
			int i = findField(fieldName, filePath);
			if(i >= 0) {lines.set(i, fieldName + ":" + newValue);}
			if(i < 0) {lines.add(fieldName + ":" + newValue);}
			
			data = String.join("\n", lines);
			System.out.println(data);
			ff.encryptAndSave(filePath, data, null, true, false);
			return 1;
			
		} catch (Exception e) {
			System.out.println(ANSI_YELLOW + "<MetadataParser.setField> Errore nella scrittura del file." + ANSI_RESET);
			e.printStackTrace();
			return -1;
		}
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	List<String> getFieldNames(String filePath){
		try{
			List<String> output = fileToList(filePath);
			if(output == null) {return null;}
			for(int i = 0; i < output.size(); i++) {
				System.out.println(output.get(i));
				tok = new StringTokenizer(output.get(i), ":");
				if(tok.hasMoreElements()) {output.set(i, tok.nextToken());} //prendo il primo token
			}
			return output;
			} catch (Exception e) {
				System.out.println("<MetadataParser.getFieldNames> Errore nella lettura del file.");
				e.printStackTrace();
				return null;
			}
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	List<String> fileToList(String filePath) {
		try{
			List<String> output = null;
			//ff = new FileFacade();
			String data = ff.loadAndDecrypt(filePath, null, false);
			if(data != null) {output = new ArrayList<String>(Arrays.asList(data.split("\n")));}
			return output;
		} catch (Exception e) {
			System.out.println("<MetadataParser.fileToList> Errore nella scrittura del file.");
			e.printStackTrace();
			return null;
		}
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	int appendToFile(String filePath, String line) { //mi creo una funzione mia per farla interfacciare con FileFacade
		try{
			//ff = new FileFacade();
			String data;
			List<String> lines = fileToList(filePath);
			lines.add(line);
			data = String.join("\n", lines);
			ff.encryptAndSave(filePath, data, null, true, false);
			return 1;
		} catch (Exception e) {
			System.out.println("<MetadataParser.appendToFile> Errore nella scrittura del file.");
			e.printStackTrace();
			return 0;
		}
	}
	
}

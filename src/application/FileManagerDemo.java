package application;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
  File manager versione in-RAM, utilizzando un database H2
  In tutto il programma le view si passano la stessa istanza di FileFacade, 
  in modo che questo database rimanga in memoria durante la modalità demo.
*/

public class FileManagerDemo extends FileManager {
	
	Connection connection;
	Logger logger = Logger.getLogger("FileManagerDemo");
	StringBuilder bui = new StringBuilder();
	
	FileManagerDemo(){
		try {
			connection = DriverManager.getConnection("jdbc:h2:mem:;INIT=RUNSCRIPT FROM 'src/resources/sql/demofs.sql'");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Eccezione SQL nell'inizializzazione del database.");
			e.printStackTrace();
		}
	}
	
	@Override
	int save(String data, String outputPath, String fileName, Boolean confirmOverwrite) {
		int parentId = 0; 
		List<String> dirs = parsePath(outputPath);

		// creo le directory
		for(int i = 0; i < dirs.size(); i++) {
			parentId = createDirectory(dirs.get(i), parentId);
		}

		try {
			// controllo se il file esiste già. non uso checkForFile() perchè voglio trovarmi l'ID del file.
			try (PreparedStatement check = connection.prepareStatement(
					"SELECT id FROM demoFileSystem WHERE name = ? AND parent_id = ? AND is_directory = FALSE")) {
	            
				check.setString(1, fileName);
				check.setInt(2, parentId);
				ResultSet results = check.executeQuery();

				if (results.next()) {
					// File già esistente -> UPDATE
					int fileId = results.getInt("id");
					try (PreparedStatement update = connection.prepareStatement(
							"UPDATE demoFileSystem SET data = ? WHERE id = ?")) {
	                    
						Blob blob = connection.createBlob();
						blob.setBytes(1, data.getBytes());
						update.setBlob(1, blob);
						update.setInt(2, fileId);
						update.executeUpdate();

						logger.log(Level.INFO, "File {0} aggiornato nella directory {1}", new String[]{fileName, outputPath});
						return 2; // 2 = aggiornato
					}
				}
			}

			// se non esiste → INSERT
			try (PreparedStatement insert = connection.prepareStatement(
					"INSERT INTO demoFileSystem (name, parent_id, is_directory, data) VALUES (?, ?, FALSE, ?)")) {
	            
				insert.setString(1, fileName);
				insert.setInt(2, parentId);
	            
				Blob blob = connection.createBlob();
				blob.setBytes(1, data.getBytes());
				insert.setBlob(3, blob);

				insert.executeUpdate();
				logger.log(Level.INFO, "File {0} creato nella directory {1}", new String[]{fileName, outputPath});
				return 1; // 1 = creato
			}

		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Errore SQL in save()", e);
			return 0; // 0 = errore
		}
	}


	@Override
	String load(String inputPath) {
		List<String> dirs = parsePath(inputPath);
		ResultSet results = null;
		try(PreparedStatement ps = connection.prepareStatement("SELECT name, data, id FROM demoFileSystem WHERE name = ? AND parent_id = ?")){
			ps.setInt(2, 0); //inizio la ricerca da 0 (root)
			for(int i = 0; i < dirs.size(); i++) {
				ps.setString(1, dirs.get(i));
				results = ps.executeQuery();
				if(results.next()) {ps.setInt(2, results.getInt("id"));} //continuo la ricerca sulla directory trovata
				else {
					logger.log(Level.SEVERE, "File {0} non trovato.", inputPath);
					printFileStructure();
					return null;
				}
			}
		logger.log(Level.INFO, "Apro il file: {0}", results.getString("name"));
		
		//lettura file
		String s = "";
		InputStream stream = results.getBlob("data").getBinaryStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		
		bui.setLength(0); //resetto lo stringbuilder
		while(true) {
			s = br.readLine();
			if(s == null) {break;}
			bui.append(s);
		}
		return bui.toString();
			
		}catch(Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Errore nell'apertura del file");
		}
		return null;
	}
	
	Boolean checkForFile(String path){
		List<String> dirs = parsePath(path);
		if(dirs.isEmpty()) return false;

		ResultSet results = null;

			try(PreparedStatement ps = connection.prepareStatement(
				"SELECT id, is_directory FROM demoFileSystem WHERE name = ? AND parent_id = ?")) {

			int parentId = 0; // root

			// Scorro tutti i token del path tranne l'ultimo (che sarà il file)
			for(int i = 0; i < dirs.size() - 1; i++) {
				ps.setString(1, dirs.get(i));
				ps.setInt(2, parentId);
				results = ps.executeQuery();
				if(results.next() && results.getBoolean("is_directory")) {
					parentId = results.getInt("id"); // entro nella directory
				} else { // directory non trovata
					return false;
				}
			}

			// Ora controllo l'ultimo token (il file vero e proprio)
			String fileName = dirs.get(dirs.size() - 1);
			ps.setString(1, fileName);
			ps.setInt(2, parentId);
			results = ps.executeQuery();
			
			return (results.next() && !results.getBoolean("is_directory"));
			// true: trovato un file con quel nome;  false: non trovato o è una directory


		} catch(SQLException e) {
			e.printStackTrace();
		}

		return false;
	}
	
	int createDirectory(String dirName, int parentId) { //crea una directory, se non esiste già                                                             
		try(PreparedStatement ps = connection.prepareStatement("SELECT id FROM demoFileSystem WHERE name = ? AND parent_id = ? AND is_directory = TRUE")){
			ps.setString(1, dirName);
			ps.setInt(2, parentId);
			ResultSet results = ps.executeQuery();
			
			if(results.next()) { //se ci sono risultati, la directory esiste già
				return results.getInt("id");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		//...se la cartella non esiste: 																												vv per far restituire l'id della nuova cartella
		try(PreparedStatement ps = connection.prepareStatement("INSERT INTO demoFileSystem (name, parent_id, is_directory) VALUES (?, ?, TRUE)", Statement.RETURN_GENERATED_KEYS)){
			ps.setString(1, dirName);
			ps.setInt(2, parentId);
			ps.executeUpdate();
			ResultSet ids = ps.getGeneratedKeys();
			if(ids.next()) {return ids.getInt(1);} //restituisco l'id della directory appena creata
//				  ^^ l'indice nei resultset inizia a -1
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		
		return -1;
	}
	
	List<String> parsePath(String outputPath){
		StringTokenizer tok = new StringTokenizer(outputPath, "/");
		List<String> list = new ArrayList<>();
		while(tok.hasMoreTokens()) {
			list.add(tok.nextToken());
		}
		return list;
	}
	
	//da rimuovere --------vv
	
	void printFileStructure() {
		try{
			Statement s = connection.createStatement();
			
	        String query = "SELECT * FROM demoFileSystem";
	        ResultSet rs = s.executeQuery(query);
	        while (rs.next()) {
	        	for(int i = 0; i < rs.getInt("parent_id"); i++) {
	        		System.out.printf(" ");
	        	}
	            System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Parent: " + rs.getInt("parent_id"));
	        }
			
		}catch(SQLException e){
			System.out.println("<FileManagerDemo.save> Eccezione SQL");
			e.printStackTrace();
		}
	}
	
	void testDB() {   									              
		try{
			logger.log(Level.INFO, "Validità connessione DB: {0}", connection.isValid(0));
		}catch(SQLException e){
			e.printStackTrace();
		}
		
	}
	
}

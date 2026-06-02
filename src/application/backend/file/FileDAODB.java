package application.backend.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystemException;
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
  In tutto il programma le view si passano la stessa istanza di FileFacade, come un testimone.
  In questo modo il database rimane in memoria durante la modalità demo.
*/

public class FileDAODB extends FileDAO {
	
	Connection connection;
	Logger logger = Logger.getLogger("FileManagerDemo");
	StringBuilder bui = new StringBuilder();
	
	FileDAODB(){
		try {
			connection = DriverManager.getConnection("jdbc:h2:mem:demoFS;INIT=RUNSCRIPT FROM 'src/resources/sql/demofs.sql'");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Eccezione SQL nell'inizializzazione del database.");
			e.printStackTrace();
		}
	}
	
	@Override
	int save(String data, String outputPath, String fileName, Boolean confirmOverwrite) throws IOException {
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
						logger.log(Level.INFO, "dati:[{0}]", data);
						return 1;
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
				logger.log(Level.INFO, "dati:[{0}]", data);
				return 1; // 1 = creato
			}

		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Errore SQL in save()", e);
			throw new IOException(e.getMessage(), e.getCause());
		}
	}


	@Override
	String load(String inputPath) throws IOException{
		List<String> dirs = parsePath(inputPath);
		ResultSet results = null;
		try(PreparedStatement ps = connection.prepareStatement("SELECT name, data, id FROM demoFileSystem WHERE name = ? AND parent_id = ?")){
			ps.setInt(2, 0); //inizio la ricerca da 0 (root)
			for(int i = 0; i < dirs.size(); i++) {
				ps.setString(1, dirs.get(i));
				results = ps.executeQuery();
				if(results.next()) {ps.setInt(2, results.getInt("id"));} //continuo la ricerca sulla directory trovata
				else {
					logger.log(Level.INFO, "File {0} inesistente. Verrà creato al salvataggio della pagina.", inputPath);
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
			bui.append("\n");
		}
		return bui.toString();
			
		}catch(SQLException e) {
			logger.log(Level.INFO, "DB: Il file esiste, ma c'è stato un errore nella lettura:", inputPath);
			throw new IOException(e.getMessage(), e.getCause());
		}
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


	void testDB() {
		try{
			logger.log(Level.INFO, "Validità connessione DB: {0}", connection.isValid(0));
		}catch(SQLException e){
			e.printStackTrace();
		}

	}
	
}

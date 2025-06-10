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

/*
  File manager versione in-RAM, utilizzando un database H2
  In tutto il programma le view si passano la stessa istanza di FileFacade, 
  in modo che questo database rimanga in memoria durante la modalità demo.

  Modalità demo:
  - Tasto nella schermata principale. manda direttamente ad una schermata calendario, per un diario demo
  - diaryPath vuoto (sarebbe il root del fs nel database)
*/

public class FileManagerDemo extends FileManager {
	
	Connection connection;
	
	FileManagerDemo(){
		try {
			connection = DriverManager.getConnection("jdbc:h2:mem:;INIT=RUNSCRIPT FROM 'src/resources/sql/demofs.sql'");
			save("", "", "diaryList", true);
		} catch (SQLException e) {
			System.out.println("<FileManagerDemo.save> Eccezione SQL");
			e.printStackTrace();
		}
	}
	
	@Override
	int save(String data, String outputPath, String fileName, Boolean confirmOverwrite) {
		//preparazione variabili per essere usate da sql
		int parentId = 0; //1 : root
		List<String> dirs = parsePath(outputPath);
		
		//creo le directory               
		for(int i = 0; i < dirs.size(); i++) {
			parentId = createDirectory(dirs.get(i), parentId);
		}
		
		//controllo se nella directory c'è già il file. sposta in checkForFile
		try(PreparedStatement ps = connection.prepareStatement("SELECT id FROM demoFileSystem WHERE name = ? AND parent_id = ? AND is_directory = FALSE")){
			ps.setString(1, fileName);
			ps.setInt(2, parentId);
			ResultSet results = ps.executeQuery();
			
			if(results.next()) { //se ci sono risultati, la directory esiste già
				System.out.println(fileName + " esiste già.");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		//creo il file
		try(PreparedStatement ps = connection.prepareStatement("INSERT INTO demoFileSystem (name, parent_id, is_directory, data) VALUES (?, ?, FALSE, ?)")){
			ps.setString(1, fileName);
			ps.setInt(2, parentId);
			
			Blob blob = connection.createBlob();
			blob.setBytes(1, data.getBytes());
			ps.setBlob(3, blob);
			
			ps.executeUpdate();
			System.out.printf("<FileManagerDemo.save> File %s salvato nella directory %s%n", fileName, outputPath);
			return 1;
			
		}catch(SQLException e) {
			System.out.println("<FileManagerDemo.save> Errore nella creazione del file");
			e.printStackTrace();
			return 0;
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
					System.out.printf("<FileManagerDemo.load> File %s non trovato.%n", inputPath);
					printFileStructure();
					return null;
				}
			}
		System.out.println("<FileManagerDemo.load> apro il file: " + results.getString("name"));
		
		//lettura file
		String data = "";
		String s = "";
		InputStream stream = results.getBlob("data").getBinaryStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			while(1 == 1) {
			s = br.readLine();
			if(s == null) {break;}
			data += s;
		}
		System.out.println("<FileManagerDemo.load> dati: " + data);
		return data;
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	Boolean checkForFile(String path){
		return false;
	}
	
	int createDirectory(String dirName, int parentId) { //crea una directory, se non esiste già                                                             
		try(PreparedStatement ps = connection.prepareStatement("SELECT id FROM demoFileSystem WHERE name = ? AND parent_id = ? AND is_directory = TRUE")){
			ps.setString(1, dirName);
			ps.setInt(2, parentId);
			ResultSet results = ps.executeQuery();
			
			if(results.next()) { //se ci sono risultati, la directory esiste già
				//System.out.println(results.getString("name") + " esiste già.");
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
		List<String> list = new ArrayList<String>();
		while(tok.hasMoreTokens()) {
			list.add(tok.nextToken());
		}
		return list;
	}
	
	//da levare --------vv
	
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
			System.out.println("Validità connessione DB: " + connection.isValid(0));
		}catch(SQLException e){
			System.out.println("<FileManagerDemo> Eccezione SQL");
			e.printStackTrace();
		}
		
	}
	
}

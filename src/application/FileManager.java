package application;

public abstract class FileManager {
	
	abstract int save(String data, String outputPath, String fileName, Boolean confirmOverwrite);
	abstract String load(String inputPath);

}

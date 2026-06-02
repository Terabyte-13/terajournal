package application.backend.file;

import java.io.IOException;
import java.nio.file.FileSystemException;

public abstract class FileDAO {
	
	abstract int save(String data, String outputPath, String fileName, Boolean confirmOverwrite) throws IOException;
	abstract String load(String inputPath) throws IOException;
	abstract Boolean checkForFile(String path);

}

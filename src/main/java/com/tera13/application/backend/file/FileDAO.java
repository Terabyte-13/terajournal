package com.tera13.application.backend.file;

import java.io.IOException;

public abstract class FileDAO {
	
	abstract int save(String data, String outputPath, String fileName) throws IOException;
	abstract String load(String inputPath) throws IOException;
	abstract Boolean checkForFile(String path);

}

package com.tera13.application.bean;

import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileBean {
	private String data;
	private String path;
	private String key;

	Logger logger = Logger.getLogger("FileBean");
	
	public void setData(String s) {
		if(s == null) {
			data = ""; //interpreto dati null come una stringa vuota
		}else {
			data = s;
		}
	}
	
	public String getData() {
		return data;
	}
	
	//-----------------------------
	
	public void setPath(String s) {
		if(s == null){
			logger.log(Level.WARNING, "Un FileBean ha percorso NULL!");
		}
		path = s;
	}
	
	public String getPath() {
		return path;
	}
	
	//------------------------------
	
	public void setKey(String s) {
		key = s;
	}
	
	public String getKey() {
		return key;
	}
	
	//-----------------------------
	
}

package application;

public class FileBean {
	private String data;
	private String path;
	private String key;

	
	
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

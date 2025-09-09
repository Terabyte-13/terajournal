package application;

public class HasherBean {
	private String string;
	private String algorithm;
	
	public void setString(String s) {
		if(s == null) {
			string = ""; //interpreto dati null come una stringa vuota
		}else {
			string = s;
		}
	}
	
	public String getString() {
		return string;
	}
	
	public void setAlgorithm(String s) {
		if(s == "MD5" || s == "SHA-256") {
			algorithm = s;
		}else {
			throw new IllegalArgumentException("Algoritmo di hashing invalido!");
		}
		
	}
	
	public String getAlgorithm() {
		return algorithm;
	}
}

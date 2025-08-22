package application;

public class HasherBean {
	private String string;
	private String algorithm;
	
	public void setString(String s) {
		string = s;
	}
	
	public String getString() {
		return string;
	}
	
	public void setAlgorithm(String s) {
		algorithm = s;
	}
	
	public String getAlgorithm() {
		return algorithm;
	}
}

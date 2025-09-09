package application;

//le view mandano il bean con fieldName e path. MetadataParser estrae il field richiesto e lo restituisce su fieldData
public class MetadataBean {
	private String fieldName;
	private String fieldData;
	private String path;

	
	public void setFieldName(String s) {
		if(s == null) {
			throw new IllegalArgumentException("Il field metadati ricercato e' invalido!");
		}else {
			fieldName = s;
		}
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	//-----------------------------
	
	public void setFieldData(String s) {
		if(s == null) {
			fieldData = ""; //interpreto dati null come una stringa vuota
		}else {
			fieldData = s;
		}
	}
	
	public String getFieldData() {
		return fieldData;
	}
	
	//-----------------------------
	
	public void setPath(String s) {
		if(s == null) {
			throw new IllegalArgumentException("Il percorso del file metadati specificato è invalido!");
		}else {
			path = s;
		}
	}
	
	public String getPath() {
		return path;
	}
	

}

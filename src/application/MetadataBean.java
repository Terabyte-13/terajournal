package application;

//le view mandano il bean con fieldName e path. MetadataParser estrae il field richiesto e lo restituisce su fieldData
public class MetadataBean {
	private String fieldName;
	private String fieldData;
	private String path;

	
	public void setFieldName(String s) {
		fieldName = s;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	//-----------------------------
	
	public void setFieldData(String s) {
		fieldData = s;
	}
	
	public String getFieldData() {
		return fieldData;
	}
	
	//-----------------------------
	
	public void setPath(String s) {
		path = s;
	}
	
	public String getPath() {
		return path;
	}
	

}

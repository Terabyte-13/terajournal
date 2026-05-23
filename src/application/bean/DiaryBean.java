package application.bean;

public class DiaryBean {

    //TODO controlli sintattici

    String name;
    String folder;
    String pwdHash;

    public void setName(String s) {
        if(s == null) {
            throw new IllegalArgumentException("Il field metadati ricercato e' invalido!");
        }else {
            name = s;
        }
    }

    public String getName() {
        return name;
    }

//------

    public void setFolder(String s) {
        if(s == null) {
            throw new IllegalArgumentException("Il percorso ricercato e' invalido!");
        }else {
            folder = s;
        }
    }

    public String getFolder() {
        return folder;
    }

//------

    public void setPwdHash(String s) {
        pwdHash = s;
    }

    public String getPwdHash() {
        return pwdHash;
    }


}

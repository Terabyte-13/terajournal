package application.bean;

public class DiaryBean {

    //TODO controlli sintattici

    private String name;
    private String folder; //il percorso del file .jm
    private String pwdHash;
    private String key;

    public void setName(String s) {
        if(s == null) {
            throw new IllegalArgumentException("Il nome del diaryBean è invalido!");
        }else {
            name = s;
        }
    }

    public String getName() {
        return name;
    }

//------

    public void setFolder(String s) {
        if(s == null) { //TODO controllo valid path
            throw new IllegalArgumentException("Il percorso del diaryBean è invalido!");
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

//------

    public void setKey(String k) {
        if(k == null) {
            throw new IllegalArgumentException("La key del diaryBean è null!");
        }else {
            key = k;
        }
    }

    public String getKey(){
        return key;
    }


}

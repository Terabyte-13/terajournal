package application.bean;

public class CreateDiaryBean {

    private String name;
    private String path;
    private String password;
    private String confirmPassword;

    public void setName(String s) {
        if(s == null) {
            throw new IllegalArgumentException("Il nome del createDiaryBean è invalido!");
        }else {
            name = s;
        }
    }

    public String getName() {
        return name;
    }

    //------

    public void setPath(String p) {
        if(p == null) {
            throw new IllegalArgumentException("Il percorso del CreateDiaryBean è invalido!");
            //TODO incolla controllo valid path
        }else {
            path = p;
        }
    }

    public String getPath() {
        return path;
    }

    //------

    public void setPassword(String p) {
        if(p == null) { //la password puo essere vuota, ma se qualcosa la imposta a null c'è un problema
            throw new IllegalArgumentException("Il nome del diaryBean è invalido!");
        }else {
            password = p;
        }
    }

    public String getPassword() {
        return password;
    }

    //------

    public void setConfirmPassword(String p) {
        if(p == null) {
            throw new IllegalArgumentException("Il nome del diaryBean è invalido!");
        }else {
            confirmPassword = p;
        }
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

}

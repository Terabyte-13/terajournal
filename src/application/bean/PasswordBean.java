package application.bean;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PasswordBean {

    private String password;
    Logger logger = Logger.getLogger("passwordBean");

    public void setPassword(String p){
        if(p == null || p.equals("")){
            logger.log(Level.WARNING, "qualcosa è andato storto! Non dovrebbero passare password NULL o \"\" per PasswordBean!");
        }
        password = p;
    }

    public String getPassword(){
        return password;
    }

}

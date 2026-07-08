package com.tera13.application.backend.userLogin;

import com.tera13.application.backend.file.Hasher;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {

    private static final String DB_DEFAULT_PWD = "LaPassword";
    boolean demoMode = false;
    Logger logger = Logger.getLogger("userDAO");
    Hasher hasher = new Hasher();

    public String userLogin(String username, String password) {
        String url;
        String p = System.getenv().getOrDefault("APP_DB_PWD", DB_DEFAULT_PWD);

        if(Boolean.TRUE.equals(demoMode)){ //in modalità demo, il DB non ha persistenza
            url = "jdbc:h2:mem:users;INIT=RUNSCRIPT FROM 'src/main/resources/sql/login.sql'";
        } else {
            url = "jdbc:h2:./users;INIT=RUNSCRIPT FROM 'src/main/resources/sql/login.sql'";
        }

        String sql = "SELECT diary_list_path FROM Users WHERE username = ? AND hash_password = ?";


        try (Connection conn = DriverManager.getConnection(url, "sa", p);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {


            pstmt.setString(1, username);
            pstmt.setString(2, hasher.getHash(password, "MD5"));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    //utente trovato, restituisce il path
                    return rs.getString("diary_list_path");
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore nel login utente.");
            e.printStackTrace();
        }

        // utente non trovato o password non corrispondente
        return "notFound";
    }


}

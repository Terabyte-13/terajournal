package com.tera13.application.backend.UserLogin;

import com.tera13.application.bean.LoginBean;
import com.tera13.application.exception.UserLoginException;

public class LoginFacade {

    UserDAO ud = new UserDAO();

    public LoginBean userLogin(LoginBean lb) throws UserLoginException {
        String u = lb.getUsername();
        String p = lb.getPassword();
        lb.setDiaryListPath(ud.userLogin(u, p));
        return lb;
    }

}

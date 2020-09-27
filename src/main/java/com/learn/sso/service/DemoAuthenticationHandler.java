package com.learn.sso.service;

import com.learn.sso.model.Credential;
import com.learn.sso.model.DemoLoginUser;
import com.learn.sso.model.LoginUser;
import org.springframework.stereotype.Service;

@Service("demo")
public class DemoAuthenticationHandler implements IAuthenticationHandler {

    static {
        System.out.println("demo22222");
    }

    @Override
    public LoginUser authenticate(Credential credential) throws Exception {
        if ("admin".equals(credential.getParameter("username")) && "admin".equals(credential.getParameter("password"))) {
            DemoLoginUser user = new DemoLoginUser();
            user.setLoginName("admin");
            return user;
        } else {
            credential.setError("error");
            return null;
        }
    }

    @Override
    public LoginUser autoLogin(String lt) throws Exception {
        return null;
    }

    @Override
    public String loginToken(LoginUser loginUser) throws Exception {
        return null;
    }
}

package com.learn.sso.model;

import com.learn.sso.utils.DES;
import com.learn.sso.utils.MD5Util;

public class DemoLoginUser extends LoginUser {
    private String loginName;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String loginToken() throws Exception {
        final String password = "12345";

        return DES.encryptDES(loginName + ","+ MD5Util.string2Md5(password));
    }
}

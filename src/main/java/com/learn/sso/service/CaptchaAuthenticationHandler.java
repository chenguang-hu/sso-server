package com.learn.sso.service;

import com.learn.sso.model.Credential;
import com.learn.sso.model.DemoLoginUser;
import com.learn.sso.model.LoginUser;
import com.learn.sso.persistence.UserPersistence;
import com.learn.sso.utils.MD5Util;
import com.learn.sso.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("captchaAuth")
public class CaptchaAuthenticationHandler implements IAuthenticationHandler {

    @Autowired
    private UserPersistence userPersistence;

    @Override
    public LoginUser authenticate(Credential credential) throws Exception {

        // 获取session中保存的验证码
        String sessionCode = (String) credential.getSettedSessionValue();

        String captcha = credential.getParameter("captcha");

        if (!captcha.equalsIgnoreCase(sessionCode)) {
            credential.setError("验证码错误");
            return null;
        }

        String password = credential.getParameter("password");
        String password2 = MD5Util.string2Md5(MD5Util.string2Md5("admin") + sessionCode);
        if ("admin".equals(credential.getParameter("username")) && password2.equals(password)) {
            DemoLoginUser user = new DemoLoginUser();
            user.setLoginName("admin");
            return user;
        } else {
            credential.setError("账号或密码错误");
            return null;
        }
    }

    @Override
    public LoginUser autoLogin(String lt) throws Exception {

        DemoLoginUser loginUser = userPersistence.getUserByLt(lt);
        if (loginUser != null) {
            return loginUser;
        }

        // 没有匹配项表示自动登录标识无效
        return null;
    }

    // 生成自动登录标识
    @Override
    public String loginToken(LoginUser loginUser) throws Exception {
        DemoLoginUser demoLoginUser = (DemoLoginUser) loginUser;

        // 生成一个唯一标识用作lt
        String lt = StringUtil.uniqueKey();

        // 将lt更新到当前user对应的字段
        userPersistence.updateLoginToken(demoLoginUser.getLoginName(), lt);

        return lt;
    }

    // 更新持久化操作
    @Override
    public void clearLoginToken(LoginUser loginUser) throws Exception {
        DemoLoginUser demoLoginUser = (DemoLoginUser) loginUser;
        userPersistence.updateLoginToken(demoLoginUser.getLoginName(), null);
    }


}

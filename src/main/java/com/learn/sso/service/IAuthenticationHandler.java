package com.learn.sso.service;

import com.learn.sso.model.Credential;
import com.learn.sso.model.LoginUser;

public interface IAuthenticationHandler {

    /**
     * 鉴权
     * @param credential
     * @return LoginUser
     * @throws Exception
     */
    public LoginUser authenticate(Credential credential) throws Exception;


    /**
     * 自动登录
     * @param lt
     * @return
     * @throws Exception
     */
    public LoginUser autoLogin(String lt) throws Exception;

    /**
     * 生成自动登录标识
     * @param loginUser
     * @return
     * @throws Exception
     */
    public String loginToken(LoginUser loginUser) throws Exception;

    /**
     *
     * @param loginUser
     */
    public void clearLoginToken(LoginUser loginUser) throws Exception;
}

package com.learn.sso.model;

import java.io.Serializable;

/**
 * 用户对象
 */
public abstract class LoginUser implements Serializable {

    /**
     * 自动登录凭证
     *
     */
    public abstract String loginToken() throws Exception;
}

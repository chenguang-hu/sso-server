package com.learn.sso.service;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 登录页前置处理器
 *
 */
public interface IPreLoginHandler {
    public static final String SESSINO_ATTR_NAME = "login_session_attr_name";

    /**
     * 前置处理
     */
    public abstract Map<?, ?> handle(HttpSession session) throws Exception;
}

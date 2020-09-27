package com.learn.sso.controller;

import com.learn.sso.common.ClientSystem;
import com.learn.sso.common.Config;
import com.learn.sso.common.Result;
import com.learn.sso.common.TokenManager;
import com.learn.sso.model.Credential;
import com.learn.sso.model.LoginUser;
import com.learn.sso.service.IAuthenticationHandler;
import com.learn.sso.service.IPreLoginHandler;
import com.learn.sso.utils.CookieUtil;
import com.learn.sso.utils.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.rmi.runtime.Log;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@Controller
public class LoginController {

    @Resource
    private Config config;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request, String backUrl, HttpServletResponse response, ModelMap map) throws Exception {

        String vt = CookieUtil.getCookie("VT", request);

        if (vt == null) {   // VT不存在

            String lt = CookieUtil.getCookie("LT", request);

            if (lt == null) {   // VT不存在, LT也不存在

                return config.getLoginViewName();
            } else {
                // VT不存在, LT存在
                // TODO: 自动登录流程
                LoginUser loginUser = config.getAuthenticationHandler().autoLogin(lt);

                if (loginUser == null) {
                    return config.getLoginViewName();
                } else {
                    vt = authSuccess(response, loginUser, true);
                    return (String) validateSuccess(backUrl, vt, loginUser, response).getData();
                }
            }
        } else {
            // VT存在
            LoginUser loginUser = TokenManager.validate(vt);
            if (loginUser != null) {  // VT有效
                return (String) validateSuccess(backUrl, vt, loginUser, response).getData(); // 验证成功后操作
            } else {    // VT无效, 转入登录页面
                return config.getLoginViewName();
            }
        }
    }

    @RequestMapping("/preLogin")
    @ResponseBody
    public Object preLogin(HttpSession session) throws Exception {
        IPreLoginHandler preLoginHandler = config.getPreLoginHandler();
        if (preLoginHandler == null) {
            throw new Exception("没有配置preLoginHandler, 无法执行预处理");
        }
        return preLoginHandler.handle(session);
    }


    // VT验证成功或登录成功之后的操作
    private Result validateSuccess(String backUrl, String vt, LoginUser loginUser, HttpServletResponse response) throws IOException {

        // 判断是否有回调url(从其他系统跳转过来的)
        if (backUrl != null) {
            response.sendRedirect(StringUtil.appendUrlParameter(backUrl, "VT", vt));
            return null;
        } else {
            // 不存在回调的url
            // TODO:获取业务系统列表
            // map.put("sysList", null);
            // map.put("vt", vt);
            return Result.build(200, vt, loginUser);
        }
    }


    /**
     * 登录验证
     *
     * @param backUrl    回调url
     * @param rememberMe 是否自动登录
     * @param request
     * @param session
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Result login(String backUrl, Boolean rememberMe, HttpServletRequest request, HttpSession session, HttpServletResponse response) throws Exception {
        final Map<String, String[]> params = request.getParameterMap();

        // System.out.println(params.get("username")[0]);

        final Object sessionVal = session.getAttribute(Config.SESSION_ATTR_NAME);

        Credential credential = new Credential() {
            @Override
            public String getParameter(String name) {
                String[] tmp = params.get(name);
                return tmp != null && tmp.length > 0 ? tmp[0] : null;
            }

            @Override
            public String[] getParameterValue(String name) {
                return params.get(name);
            }

            @Override
            public Object getSettedSessionValue() {
                return sessionVal;
            }
        };

        IAuthenticationHandler handler = config.getAuthenticationHandler();
        LoginUser loginUser = handler.authenticate(credential);

        if (loginUser == null) {
            return Result.build(500, credential.getError(), null);
        } else {
            String vt = authSuccess(response, loginUser, rememberMe);
            return validateSuccess(backUrl, vt, loginUser, response);
            // return Result.ok();
        }
    }

    // 授权成功后的操作
    private String authSuccess(HttpServletResponse response, LoginUser loginUser, Boolean rememberMe) throws Exception {

        // 生成VT
        String vt = StringUtil.uniqueKey();

        // 根据rememberMe决定是否生成LT
        // TODO:自动登录标识生成
        if (rememberMe != null && rememberMe) {
            // String lt = loginUser.loginToken();
            String lt = config.getAuthenticationHandler().loginToken(loginUser);

            // 写入Cookie
            setLtCookie(lt, response);
        }

        // 存入Map
        TokenManager.addToken(vt, loginUser);

        // 写Cookie
        Cookie cookie = new Cookie("VT", vt);

        if (config.isSecureMode()) {
            cookie.setSecure(true);
        }

        response.addCookie(cookie);
        return vt;
    }

    @RequestMapping("/logout")
    public String logout(String backUrl, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 服务端从Cookie中取出VT
        String vt = CookieUtil.getCookie("VT", request);

        // 清除自动登录信息
        LoginUser loginUser = TokenManager.validate(vt);
        if (loginUser!=null){
            // 清除服务端自动登录状态
            config.getAuthenticationHandler().clearLoginToken(loginUser);

            // 清除自动登录cookie
            Cookie ltCookie = new Cookie("LT", null);
            response.addCookie(ltCookie);
        }

        // 移除token
        TokenManager.invalid(vt);

        // 移除server端vt
        Cookie cookie = new Cookie("VT", null);     // 覆盖原来的VT
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        // 通知各个客户端logout
        for (ClientSystem clientSystem : config.getClientSystems()) {
            // TODO:客户端的cookie失效
            // clientSystem.noticeLogout(vt);
        }

        if (backUrl == null) {
            return "/logout";
        } else {
            response.sendRedirect(backUrl);
            return null;
        }
    }


    private void setLtCookie(String lt, HttpServletResponse response) {
        Cookie ltCookie = new Cookie("LT", lt);
        ltCookie.setMaxAge(config.getAutoLoginExpDays() * 24 * 60 * 60);
        if (config.isSecureMode()) {
            ltCookie.setSecure(true);
        }
        response.addCookie(ltCookie);
    }

}

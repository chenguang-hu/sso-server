package com.learn.sso.common;


import com.learn.sso.common.Config;
import com.learn.sso.model.LoginUser;


import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局静态访问
 */
public class TokenManager {

    // 定时器Timer, 间隔一段时间后重复执行是否过期的验证
    // 参数true：当前的定时器线程是否为后台(守护)线程(程序运行期间有效)
    private static final Timer timer = new Timer(true);

    // 通过静态代码块启动Timer
    static {
        // 参数1：到达固定时间点后要执行的任务
        // 参数2：任务什么时候开始第一次执行, 类被加载之后一分钟启动
        // 参数3：每间隔1分钟再次执行
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (Map.Entry<String, Token> entry : DATA_MAP.entrySet()) {
                    String vt = entry.getKey();
                    Token token = entry.getValue();
                    Date expired = token.expired;
                    Date now = new Date();

                    // 当前时间大于过期时间
                    if (now.compareTo(expired) > 0) {
                        // 令牌支持自动延期服务, 应用客户端缓存机制
                        // 令牌最后访问时间是存储在客户端的, 所以服务端向所有客户端发起一次timeout通知, 携带过期时间间隔
                        // 客户端根据lastAccessTime(最后访问时间) + tokenTimeout(过期时间间隔)计算是否过期
                        // 若没有过期, 用各个客户端最大有效期更新当前时间

                        List<ClientSystem> clientSystems = config.getClientSystems();   // 获取所有的客户端系统列表

                        Date maxClientExpired = expired;    // 最大的过期时间
//
//                        for (ClientSystem clientSystem : clientSystems) {
//                            Date clientExpired = clientSystem.noticeTimeout(vt, config.getTokenTimeout());
//                            if (clientExpired != null && clientExpired.compareTo(now) > 0) {
//                                // 如果客户端时间大于本来的过期时间就更新
//                                maxClientExpired = maxClientExpired.compareTo(clientExpired) < 0 ? clientExpired : maxClientExpired;
//                            }
//                        }

                        // 客户端最大过期时间大于当前时间
//                        if (maxClientExpired.compareTo(now) > 0) {
//                            token.expired = maxClientExpired;
//                        }else {
//                            // 已过期, 清除掉过期的token
//                            DATA_MAP.remove(vt);
//                        }
                    }
                }
            }
        }, 60 * 1000, 60 * 1000);
    }

    @Resource
    private static Config config;

    private TokenManager() {

    }

    // 复合结构体, 含有loginUser和过期时间expired两个成员
    private static class Token {
        private LoginUser loginUser;    // 登录用户对象
        private Date expired;   // 过期时间
    }

    // 令牌存储结构
    private static final Map<String, Token> DATA_MAP = new ConcurrentHashMap<>();


    /**
     * 验证令牌的有效性
     *
     * @param vt validate token
     * @return LoginUser
     */
    public static LoginUser validate(String vt) {
        Token token = DATA_MAP.get(vt);
        return token == null ? null : token.loginUser;
    }

    /**
     * 用户授权成功后将授权信息存入
     *
     * @param vt        validate token
     * @param loginUser 登录用户
     */
    public static void addToken(String vt, LoginUser loginUser) {
        Token token = new Token();
        token.loginUser = loginUser;

        // 非自动登录时的处理
        token.expired = new Date(new Date().getTime() + config.getTokenTimeout() * 60 * 1000);

        // TODO:自动登录时, 有效期处理

        DATA_MAP.put(vt, token);
    }

    public static void invalid(String vt) {
        if (vt != null) {
            DATA_MAP.remove(vt);
        }
    }
}

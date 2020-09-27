package com.learn.sso.common;

import com.learn.sso.service.IAuthenticationHandler;
import com.learn.sso.service.IPreLoginHandler;
import com.sun.el.parser.BooleanNode;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class Config implements ResourceLoaderAware {
    public static final String SESSION_ATTR_NAME = "login_session_attr_name";

    private ResourceLoader resourceLoader;

    // 鉴权处理器
    @Resource(name = "captchaAuth")
    private IAuthenticationHandler authenticationHandler;

    @Resource(name = "captchaPre")
    private IPreLoginHandler preLoginHandler;

    // 登录视图名
    private String loginViewName = "/login";

    // 令牌有效期, 单位为分钟, 默认30分钟
    private int tokenTimeout = 30;

    // 是否必须是https
    private boolean secureMode = true;

    // 自动登录有效期的天数
    private int autoLoginExpDays;

    // 客户系统列表
    private List<ClientSystem> clientSystems = new ArrayList<>();


    /**
     * 重新加载配置, 以支持热部署
     */
    @PostConstruct  // 启动时执行一次
    public void refreshConfig() {

        System.out.println("执行更新方法");

        // 加载config.properties
        Properties configProperties = new Properties();

        try {
            org.springframework.core.io.Resource resource = resourceLoader.getResource("classpath:config.properties");
            configProperties.load(resource.getInputStream());
        } catch (IOException e) {
            System.err.println("在classpath下没有找到配置文件config.properties");
        }

        // vt的有效期参数
        String configTokenTimeout = (String) configProperties.get("tokenTimeout");
        if (configTokenTimeout != null) {
            try {
                tokenTimeout = Integer.parseInt(configTokenTimeout);
            } catch (NumberFormatException e) {
                System.err.println("tokenTimeout参数配置不正确");
            }
        }

        // 自动登录lt的有效期参数
        String configAutoLoginExpDays = (String) configProperties.get("autoLoginExpDays");
        if (configAutoLoginExpDays != null) {
            try {
                autoLoginExpDays = Integer.parseInt(configAutoLoginExpDays);
            } catch (NumberFormatException e) {
                System.err.println("autoLoginExpDays参数配置不正确");
            }
        }

        // 是否仅https安全模式运行
        String configSecureMode = configProperties.getProperty("secureMode");
        if (configSecureMode != null) {
            try {
                secureMode = Boolean.parseBoolean(configSecureMode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 加载客户端系统配置信息
        try {
            loadClientSystems();
        } catch (Exception e) {
            System.err.println("加载client system配置失败");
            e.printStackTrace();
        }
    }

    /**
     * 应用停止时, 做清理工作, 如通知客户端logout
     */
    @PreDestroy
    public void destroy() {
        System.out.println("执行销毁方法");
//        for (ClientSystem clientSystem : clientSystems) {
//            clientSystem.noticeShutdown();
//        }
    }

    // 加载客户端系统配置列表
    private void loadClientSystems() throws Exception {
        org.springframework.core.io.Resource resource = resourceLoader.getResource("classpath:client_systems.xml");

        // dom4j
        SAXReader reader = new SAXReader();
        Document document = reader.read(resource.getInputStream());

        Element rootElement = document.getRootElement();
        List<Element> systemElements = rootElement.elements();

        // 再次加载时先清空list
        clientSystems.clear();

        for (Element element : systemElements) {

            ClientSystem clientSystem = new ClientSystem();
            clientSystem.setId(element.attributeValue("id"));
            clientSystem.setName(element.attributeValue("name"));
            clientSystem.setBaseUrl(element.elementText("baseUrl"));
            clientSystem.setHomeUri(element.elementText("homeUri"));
            clientSystem.setInnerAddress(element.elementText("innerAddress"));

            clientSystems.add(clientSystem);
        }
    }

    /**
     * 获取鉴权处理器
     *
     * @return IAuthenticationHandler
     */
    public IAuthenticationHandler getAuthenticationHandler() {
        return authenticationHandler;
    }

    public void setAuthenticationHandler(IAuthenticationHandler authenticationHandler) {
        this.authenticationHandler = authenticationHandler;
    }


    /**
     * 获取登录视图名称
     *
     * @return String
     */
    public String getLoginViewName() {
        return loginViewName;
    }

    public void setLoginViewName(String loginViewName) {
        this.loginViewName = loginViewName;
    }

    public int getTokenTimeout() {
        return tokenTimeout;
    }

    public void setTokenTimeout(int tokenTimeot) {
        this.tokenTimeout = tokenTimeot;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<ClientSystem> getClientSystems() {
        return clientSystems;
    }

    public void setClientSystems(List<ClientSystem> clientSystems) {
        this.clientSystems = clientSystems;
    }

    public IPreLoginHandler getPreLoginHandler() {
        return preLoginHandler;
    }

    public void setPreLoginHandler(IPreLoginHandler preLoginHandler) {
        this.preLoginHandler = preLoginHandler;
    }

    public boolean isSecureMode() {
        return secureMode;
    }

    public void setSecureMode(boolean secureMode) {
        this.secureMode = secureMode;
    }

    public int getAutoLoginExpDays() {
        return autoLoginExpDays;
    }

    public void setAutoLoginExpDays(int autoLoginExpDays) {
        this.autoLoginExpDays = autoLoginExpDays;
    }
}

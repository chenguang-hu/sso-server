package com.learn.sso.common;

import java.io.Serializable;

public class ClientSystem implements Serializable {
    private String id;      // 唯一标识
    private String name;    // 系统名称
    private String baseUrl; // 应用基路径, 代表应用访问的起始点
    private String homeUri; // 应用主页面url, baseUrl + homeUrl = 主页url
    private String innerAddress;    // 系统之间内部通信地址

    private String getHomeUrl() {
        return baseUrl + homeUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getInnerAddress() {
        return innerAddress;
    }

    public void setInnerAddress(String innerAddress) {
        this.innerAddress = innerAddress;
    }

    public String getHomeUri() {
        return homeUri;
    }

    public void setHomeUri(String homeUri) {
        this.homeUri = homeUri;
    }
}

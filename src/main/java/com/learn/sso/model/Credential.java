package com.learn.sso.model;


/**
 * 对登录页面提交的内容进行集中存储, 并提供特定获取方法的一个实体类
 */
public abstract class Credential {
    // 错误信息
    private String error;

    // 获取一个参数值
    public abstract String getParameter(String name);

    // 获取多个参数值
    public abstract String[] getParameterValue(String name);

    // PreLoginHandler通过setSessionValue()方法写入特定session的属性值
    public abstract Object getSettedSessionValue();

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

package com.learn.sso.controller;

import com.learn.sso.common.Config;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;


/**
 * 演示配置管理的Controller
 */
@Controller
public class ConfigManagerController {

    @Resource
    private Config config;

    @RequestMapping("/config")
    public String configPage() {
        return "config";
    }

    @RequestMapping("/config/refresh")
    @ResponseBody
    public boolean config(String code) {
        if ("test".equals(code)) {
            config.refreshConfig();
            return true;
        }
        return false;
    }
}

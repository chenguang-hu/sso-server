package com.learn.sso.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

public class StringUtil {

    public static String appendUrlParameter(String origUrl, String parameterName, String parameterVal) {
        if (origUrl == null) {
            return null;
        }

        String bound = origUrl.contains("?") ? "&" : "?";
        try {
            return origUrl + bound + parameterName + "=" + URLEncoder.encode(parameterVal, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String uniqueKey(){
        return UUID.randomUUID() + "";
    }
}

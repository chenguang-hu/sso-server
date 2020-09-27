var Cookie = {
    /**
     * 按名称获取Cookie值
     */
    get: function (name) {
        var cookies = document.cookie;
        cookies = cookies.split("; ");
        for (var i = 0, len = cookies.length; i < len; ++i){
            var cookieItem = cookies[i];
            var pos = cookieItem.indexOf("=");
            var cname = cookieItem.substring(0, pos);
            var cval = unescape(cookieItem.substr(pos + 1));

            if (name == cname) {
                return cval;
            }
        }
    },

    /**
     * 写入cookie
     * @param name
     * @param val
     * @param path
     * @param expMinute
     * @param domain
     * @param secure
     */
    set: function (name, val, path, expMinute, domain, secure) {
        var cookieItem = name + "=" + escape(val);
        if (path) {
            cookieItem += ";path=" + path;
        }

        if (expMinute) {
            cookieItem += ";expires=" + new Date(new Date().getTime() + expMinute * 60 * 1000)
        }

        if (domain) {
            cookieItem += ";domain=" + domain;
        }

        if (secure) {
            cookieItem += ";secure";
        }

        document.cookie = cookieItem;
    },

    /**
     * 删除cookie
     * @param name
     */
    del: function (name) {
        document.cookie = name + "=anyVal;expires=" + new Date(0).toUTCString();
    }
};
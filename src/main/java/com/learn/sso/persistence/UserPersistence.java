package com.learn.sso.persistence;

import com.learn.sso.dao.UserDao;
import com.learn.sso.model.DemoLoginUser;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;


@Component
public class UserPersistence {

    @Resource
    private UserDao userDao;

    /**
     * 更新数据库当前登录用户的lt标识
     * @param loginName 唯一账户名
     * @param lt    login_token
     * @throws Exception
     */
    public void updateLoginToken(String loginName, String lt) throws Exception {
        userDao.updateLoginToken(lt, loginName);
    }


    /**
     * 按自动登录标识查询用户信息
     * @param lt 自动登录标识
     * @return
     */
    public DemoLoginUser getUserByLt(String lt) {
        Map<String, Object> user_info = userDao.findByLt(lt);
        if (user_info!=null){
            DemoLoginUser loginUser = new DemoLoginUser();
            loginUser.setLoginName(user_info.get("username").toString());
            return loginUser;
        }

        return null;
    }
}

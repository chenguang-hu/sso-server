package com.learn.sso.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface UserDao {
    Map<String, Object> findByUsername(String username);

    Map<String, Object> findByLt(String login_token);

    public void updateLoginToken(@Param("token") String token, @Param("username") String username);
}

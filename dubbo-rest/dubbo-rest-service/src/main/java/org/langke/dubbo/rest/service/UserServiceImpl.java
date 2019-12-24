package org.langke.dubbo.rest.service;

import org.langke.dubbo.rest.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@org.apache.dubbo.config.annotation.Service
public class UserServiceImpl implements UserService{
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Override
    public Boolean registerUser(User user) {
        logger.info("registerUser:{}",user.getUserName());
        return true;
    }

    @Override
    public User getUser(Long id) {
        logger.info("getUser:{}",id);
        User user = new User();
        user.setUserName("Hello Name 用户名");
        user.setId(id);
        return user;
    }
}

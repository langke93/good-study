package org.langke.dubbo.rest.client;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.langke.dubbo.rest.bean.User;
import org.langke.dubbo.rest.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceClientImpl implements UserServiceClient {
    Logger logger = LoggerFactory.getLogger(UserServiceClientImpl.class);
    @Reference(url = "dubbo://10.0.75.1:20880/org.langke.dubbo.rest.service.UserService")
    private UserService userService;
    @Override
    public Boolean registerUser(User user) {
        logger.info("client registerUser:{}",user.getUserName());
        return userService.registerUser(user);
    }

    @Override
    public User getUser(Long id) {
        logger.info("client getUser:{}",id);
        return userService.getUser(id);
    }
}

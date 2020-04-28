package org.langke.dubbo.rest.client;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.langke.dubbo.rest.bean.User;
import org.langke.dubbo.rest.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserServiceClientImpl implements UserServiceClient {
    Logger logger = LoggerFactory.getLogger(UserServiceClientImpl.class);
    @Reference()
    private UserService userService;
    @Override
    public Boolean registerUser(User user, HttpServletRequest request) {
        logger.info("client {} registerUser:{}",request.getRemoteAddr(),user.getUserName());
        return userService.registerUser(user);
    }

    @Override
    public User getUser(Long id, HttpServletRequest request) {
        logger.info("client {} getUser:{}",request.getRemoteAddr(),id);
        return userService.getUser(id);
    }
}

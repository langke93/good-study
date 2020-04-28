package org.langke.dubbo.rest.service;

import org.apache.dubbo.rpc.RpcContext;
import org.langke.dubbo.rest.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@org.apache.dubbo.config.annotation.Service
public class UserServiceImpl implements UserService{
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Override
    public Boolean registerUser(User user) {
        logger.info("registerUser:{} ip:{}",user.getUserName(),RpcContext.getContext().getRemoteAddressString());
        return true;
    }

    @Override
    public User getUser(Long id) {
        logger.info("getUser:{} ip:{}",id, RpcContext.getContext().getRemoteAddressString());
        User user = new User();
        user.setUserName("Hello Name 用户名");
        user.setId(id);
        return user;
    }
}

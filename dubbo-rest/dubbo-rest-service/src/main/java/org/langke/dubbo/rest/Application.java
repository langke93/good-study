package org.langke.dubbo.rest;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath:/spring-*.xml"});
        context.start();
        System.in.read(); // 按任意键退出
    }
}

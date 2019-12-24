package org.langke.spring.boot.rest.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author langke on 2019/9/23.
 */
@ConfigurationProperties(prefix = "app.config")
@Component
public class AppConfig {


    /**
     * 代理主机
     */
    private List<String> proxyList;

    public List<String> getProxyList() {
        return proxyList;
    }

    public void setProxyList(List<String> proxyList) {
        this.proxyList = proxyList;
    }

}

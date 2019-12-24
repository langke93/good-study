package org.langke.spring.boot.rest;

import org.langke.spring.boot.rest.util.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication(scanBasePackages = {"org.langke.spring.boot.rest"})
@EnableConfigurationProperties({AppConfig.class})
@RestController
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
package org.langke.spring.boot.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.langke.spring.boot.rest.service.DataService;
import org.langke.spring.boot.rest.util.OkHttpReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 控制层
 * @author langke on 2019/9/23.
 */
@RestController
@Slf4j
public class DataController {

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);
    @Autowired
    DataService dataService;


    @Autowired
    OkHttpReq okHttpReq;

    @RequestMapping("test")
    public String test(){
        return "test!";
    }

    @RequestMapping("getByURL")
    public String getByURL(String url) {
        return okHttpReq.get(url, String.class,null).get();
    }

    @RequestMapping("count")
    public Integer countTableColumns(String tableName){
        return dataService.countTableColumns(tableName);
    }
}

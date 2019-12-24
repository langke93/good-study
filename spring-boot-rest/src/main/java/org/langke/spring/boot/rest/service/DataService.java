package org.langke.spring.boot.rest.service;

import org.langke.spring.boot.rest.dao.TableNameDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataService {
    private static final Logger logger = LoggerFactory.getLogger(DataService.class);
    @Autowired
    private TableNameDao tableNameDao;

    public Integer countTableColumns(String tableName){
        return tableNameDao.count(tableName);
    }
}

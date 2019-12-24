package org.langke.spring.boot.rest.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TableNameDao {
    private static final Logger logger = LoggerFactory.getLogger(TableNameDao.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Integer count(String tableName){
        String sql = "select count(*) from "+tableName+" where 1=1 ";
        return jdbcTemplate.queryForObject(sql,Integer.class);
    }
}

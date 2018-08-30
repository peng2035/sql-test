package com.bobby.peng.sql.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by bobby.peng on 2018/8/29.
 */
@Service
public class SqlService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertSimple(long id, int num) {
        String sql = "insert into simple_sql_insert (id,num) values (?,?)";
        jdbcTemplate.update(sql, id, num);
    }

    public Long getMaxId() {
        String sql = "select max(id) as id from simple_sql_insert";
        Long id = jdbcTemplate.queryForObject(sql, Long.class);
        if (id != null) return id + 1l;
        return 1l;
    }

    public void insertDeletedTable(long id) {
        String sql = "insert into simple_delete_index (id, is_deleted, remark, name, merchant_id) values (?,?,?,?,?)";
        boolean is_deleted = (id % 2 == 1 ? false : true);

        jdbcTemplate.update(sql, id, is_deleted, id + "remark", id + "name", id);
    }

    public Long getDeletedTableMaxId() {
        String sql = "select max(id) as id from simple_delete_index";
        Long id = jdbcTemplate.queryForObject(sql, Long.class);
        if (id != null) return id + 1l;
        return 1l;
    }
}

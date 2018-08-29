package com.bobby.peng.sql.test.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;

/**
 * Created by bobby.peng on 2018/8/29.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SqlControllerTest {

    @Autowired
    private SqlController sqlController;

    @Test
    public void eachTest() {
        sqlController.each();
    }


    @Test
    public void poolTest() throws ExecutionException, InterruptedException {
        sqlController.pool();
    }
}

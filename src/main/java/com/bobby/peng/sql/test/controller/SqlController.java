package com.bobby.peng.sql.test.controller;

import com.bobby.peng.sql.test.service.SqlService;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bobby.peng on 2018/8/29.
 */
@RestController
@RequestMapping("/sql")
public class SqlController {

    @Autowired
    private SqlService sqlService;

    @RequestMapping("/each")
    public void each() {
        long id = sqlService.getMaxId();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; id++, i++) {
            sqlService.insertSimple(id, i);
        }
        System.out.println(System.currentTimeMillis() - startTime);
    }

    @RequestMapping("/pool")
    public void pool() throws ExecutionException, InterruptedException {
        ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(20
                , new DefaultThreadFactory()));
        ;
        long startTime = System.currentTimeMillis();
        List<ListenableFuture<Integer>> futures = Lists.newArrayList();

        long id = sqlService.getMaxId();
        for (int i = 0; i < 1000; id++, i++) {
            long finalId = id;
            int finalI = i;
            try {
                futures.add(pool.submit(() -> {
                    sqlService.insertSimple(finalId,finalI);
                    return 1;
                }));
            } catch (Exception e) {
                System.out.println(e);
            }

        }
        System.out.println("总记录 ： " + Futures.successfulAsList(futures).get().size());
        System.out.println(System.currentTimeMillis() - startTime);
    }

    class DefaultThreadFactory implements ThreadFactory {
        private final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "Excel-" + "pool-" +
                    String.format("%02d", poolNumber.getAndIncrement()) +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + String.format("%03d", threadNumber.getAndIncrement()), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}

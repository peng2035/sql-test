package com.bobby.peng.sql.test;

import com.bobby.peng.sql.test.service.SqlService;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SqlTestApplicationTests {

    @Autowired
    private SqlService sqlService;

    @Test
    public void contextLoads() {
    }


    @Test
    public void testSimpleSqlEachTime() {
        long id = sqlService.getMaxId();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; id++, i++) {
            sqlService.insertSimple(id, i);
        }
        System.out.println(System.currentTimeMillis() - startTime);
    }

    @Test
    public void testSimpleSqlInThreadPool() throws ExecutionException, InterruptedException {
        ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(20
                , new DefaultThreadFactory()));
        ;
        long startTime = System.currentTimeMillis();
        List<ListenableFuture<Integer>> futures = Lists.newArrayList();

        long id = sqlService.getMaxId();
        for (int i = 0; i < 10000; id++, i++) {
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

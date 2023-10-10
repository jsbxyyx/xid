package com.github.jsbxyyx.xid;

import javax.sql.DataSource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.mysql.cj.jdbc.MysqlDataSource;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class XidTests {

    private static final Map<Long, Object> map = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser("root");
        dataSource.setPassword("root");
        dataSource.setURL("jdbc:mysql://mysql:3306/test?characterEncoding=utf-8&serverTimezone=UTC&useSSL=false");

        Xid.setDataSource(dataSource);

        new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                long id = Xid.get();
                System.out.println(Thread.currentThread().getName() + " : " + id);
                boolean b = map.containsKey(id);
                if (b) {
                    System.out.println(Thread.currentThread().getName() + " : " + id + "======");
                } else {
                    map.put(id, 0);
                }
            }
        }, "t1").start();

        new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                long id = Xid.get();
                System.out.println(Thread.currentThread().getName() + " : " + id);
                boolean b = map.containsKey(id);
                if (b) {
                    System.out.println(Thread.currentThread().getName() + " : " + id + "======");
                } else {
                    map.put(id, 0);
                }
            }
        }, "t2").start();

        TimeUnit.SECONDS.sleep(60000);
    }

}

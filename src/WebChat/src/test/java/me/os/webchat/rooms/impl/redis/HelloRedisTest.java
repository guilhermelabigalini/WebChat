/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.os.webchat.rooms.impl.redis;

import java.util.concurrent.Executors;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 *
 * @author guilherme
 */
public class HelloRedisTest {

    public HelloRedisTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

//https://github.com/redisson/redisson
//https://mvnrepository.com/artifact/com.lambdaworks/lettuce
//http://www.programcreek.com/java-api-examples/index.php?api=redis.clients.jedis.JedisPubSub
    //@Test
    public void connect_set() {
        Jedis jedis = new Jedis("localhost", 6379, 0);

        jedis.connect();

        //jedis.lpush(key, strings)
        //http://redis.io/commands/srem
        //http://redis.io/commands/sadd
        //http://redis.io/commands/smembers
        //jedis.sadd(key, members)
        //jedis.srem(key, members)
        
        System.out.println("Connected to Redis");

        String value = "my value";

        jedis.set("key", value);

        String v = jedis.get("key");

        assertEquals(value, v);

//        jedis.subscribe(new JedisPubSub() {
//            
//}), channels);
    }

    //@Test
    public void pub_sub() throws InterruptedException {
        final String channel = "room";
        int totalMessages = 10;

        MySub sub1 = new MySub("sub1");
        MySub sub2 = new MySub("sub2");

        Runnable run1 = () -> {
            Jedis jedisSub = new Jedis("localhost", 6379, 0);
            jedisSub.subscribe(sub1, channel);
        };

        Runnable run2 = () -> {
            Jedis jedisSub = new Jedis("localhost", 6379, 0);
            jedisSub.subscribe(sub2, channel);
        };

        Thread t1 = new Thread(run1);
        t1.start();
        Thread t2 = new Thread(run2);
        t2.start();

        Thread.sleep(1000);
        
        Jedis jedisPub = new Jedis("localhost", 6379, 0);

        for (int i = 1; i <= totalMessages; i++) {
            jedisPub.publish(channel, "message " + Integer.toString(i));
        }

        Thread.sleep(1000);

        assertEquals(totalMessages, sub2.receivedMessages);
        assertEquals(totalMessages, sub1.receivedMessages);
        
        t2.interrupt();
        t1.interrupt();
    }

    public static class MySub extends JedisPubSub {

        public int receivedMessages = 0;
        private final String name;

        public MySub(String name) {
            this.name = name;
        }

        @Override
        public void onMessage(String channel, String message) {
            receivedMessages++;
            java.time.LocalDateTime ldt = java.time.LocalDateTime.now();

            System.out.println(ldt + " - listener " + name + " received: " + message);
        }
    }
}

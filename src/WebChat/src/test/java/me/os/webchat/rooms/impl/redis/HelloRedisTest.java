/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.os.webchat.rooms.impl.redis;

import java.util.List;
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
        JedisFactory.getInstance().useResource(jedis -> {
            jedis.flushAll();
        });
    }

//https://github.com/redisson/redisson
//https://mvnrepository.com/artifact/com.lambdaworks/lettuce
//http://www.programcreek.com/java-api-examples/index.php?api=redis.clients.jedis.JedisPubSub
    //@Test
    public void listTest() {

        String listName = "mylist";
        String item1 = " item numer ber";
        String item2 = " item 2";
        String item3 = "item number 3";

        JedisFactory.getInstance().useResource(jedis -> {
            jedis.del(listName);
        });

        JedisFactory.getInstance().useResource(jedis -> {
            jedis.lpush(listName, item1);
        });

        JedisFactory.getInstance().useResource(jedis -> {
            assertEquals(1l, (long) jedis.llen(listName));
        });

        JedisFactory.getInstance().useResource(jedis -> {
            jedis.lpush(listName, item2);
        });

        JedisFactory.getInstance().useResource(jedis -> {
            assertEquals(2l, (long) jedis.llen(listName));
        });

        JedisFactory.getInstance().useResource(jedis -> {
            jedis.lpush(listName, item3);
        });

        JedisFactory.getInstance().useResource(jedis -> {
            assertEquals(3, (long) jedis.llen(listName));

            List<String> elements = jedis.lrange(listName, 0, 1000);
            assertTrue(elements.contains(item1));
            assertTrue(elements.contains(item2));
            assertTrue(elements.contains(item3));
        });

        JedisFactory.getInstance().useResource(jedis -> {
            jedis.lrem(listName, 1, item2);
        });

        JedisFactory.getInstance().useResource(jedis -> {
            assertEquals(2, (long) jedis.llen(listName));

            List<String> elements = jedis.lrange(listName, 0, 1000);
            assertTrue(elements.contains(item1));
            assertFalse(elements.contains(item2));
            assertTrue(elements.contains(item3));
        });
    }

    //@Test
    public void connect_set() {

        JedisFactory.getInstance().useResource(jedis -> {
            System.out.println("Connected to Redis");

            String value = "my value";

            jedis.set("key", value);

            String v = jedis.get("key");

            assertEquals(value, v);
        });
    }

    //@Test
    public void pub_sub() throws InterruptedException {
        final String channel = "room";
        int totalMessages = 10;

        MySub sub1 = new MySub("sub1");
        MySub sub2 = new MySub("sub2");

        Runnable run1 = () -> {

            JedisFactory.getInstance().useResource(jedis -> {
                Jedis jedisSub = new Jedis("localhost", 6379, 0);
                jedis.subscribe(sub1, channel);
            });

        };

        Runnable run2 = () -> {
            JedisFactory.getInstance().useResource(jedis -> {
                Jedis jedisSub = new Jedis("localhost", 6379, 0);
                jedis.subscribe(sub2, channel);
            });
        };

        Thread t1 = new Thread(run1);
        t1.start();
        Thread t2 = new Thread(run2);
        t2.start();

        Thread.sleep(1000);

        JedisFactory.getInstance().useResource(jedisPub -> {
            for (int i = 1; i <= totalMessages; i++) {
                jedisPub.publish(channel, "message " + Integer.toString(i));
            }
        });

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

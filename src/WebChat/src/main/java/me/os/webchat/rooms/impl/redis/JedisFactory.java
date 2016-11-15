/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.os.webchat.rooms.impl.redis;

import java.util.function.Consumer;
import java.util.function.Function;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 *
 * @author guilherme
 */
public class JedisFactory {

    private static JedisPool jedisPool;
    private static JedisFactory instance;

    public JedisFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        jedisPool = new JedisPool(
                poolConfig,
                RedisDBConfig.HOST,
                RedisDBConfig.PORT
        );

    }

    public <T> T useResource(Function<Jedis, T> consumer) {
        try (Jedis jedis = jedisPool.getResource()) {
            return consumer.apply(jedis);
        }
    }

    public void useResource(Consumer<Jedis> consumer) {
        try (Jedis jedis = jedisPool.getResource()) {
            consumer.accept(jedis);
        }
    }

    public static JedisFactory getInstance() {
        if (instance == null) {
            instance = new JedisFactory();
        }
        return instance;
    }
}

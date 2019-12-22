package org.seckill.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import org.seckill.common.redis.KeyPrefix;

import static org.seckill.utils.ConvertUtil.beanToStr;
import static org.seckill.utils.ConvertUtil.strToBean;

/**
 * @author Zuquan Song
 *
 * @description RedisService
 */
@Service
public class RedisService {

    private final JedisPool jedisPool;

    @Autowired
    public RedisService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        try(Jedis jedis = jedisPool.getResource()) {
            String realKey = prefix.getRealKey(key);
            String str = jedis.get(realKey);
            return strToBean(str, clazz);
        }
    }

    public <T> boolean set(KeyPrefix prefix, String key, T value) {
        try(Jedis jedis = jedisPool.getResource()) {
            String str = beanToStr(value);
            if (null != str && str.length() > 0) {
                String realKey = prefix.getRealKey(key);
                int seconds = prefix.getExpireSeconds();
                if (seconds <= 0) {
                    jedis.set(realKey, str);
                } else {
                    jedis.setex(realKey, seconds, str);
                }
                return true;
            }
        }
        return false;
    }

    public boolean exists(KeyPrefix prefix, String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            String realKey = prefix.getRealKey(key);
            return jedis.exists(realKey);
        }
    }

    public Long incr(KeyPrefix prefix, String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            String realKey = prefix.getRealKey(key);
            return jedis.incr(realKey);
        }
    }

    public boolean delete(KeyPrefix prefix, String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            String realKey = prefix.getRealKey(key);
            return jedis.del(realKey) > 0;
        }
    }

    public Long decr(KeyPrefix prefix, String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            String realKey = prefix.getRealKey(key);
            return jedis.decr(realKey);
        }
    }


}

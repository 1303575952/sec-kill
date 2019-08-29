package com.doudizu.seckill.redis;

import com.alibaba.fastjson.JSON;
import com.doudizu.seckill.conf.PropertiesConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;
    @Autowired
    PropertiesConf propertiesConf;

    /**
     * 通过key拿到value
     *
     * @param prefix
     * @param key
     * @return
     */
    public String getKey(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            String str = jedis.get(realKey);
            return str;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 获取单个对象
     */
    public <T> T get(KeyPrefix prefix, int key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            String str = jedis.get(realKey);
            T t = stringToBean(str, clazz);
            return t;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 设置对象
     */
    public <T> boolean set(KeyPrefix prefix, int key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            if (str == null || str.length() <= 0) {
                return false;
            }
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            int seconds = prefix.expireSeconds();
            if (seconds <= 0) {
                jedis.set(realKey, str);
            } else {
                jedis.setex(realKey, seconds, str);
            }
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 判断key是否存在
     */
    public <T> boolean exists(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            return jedis.exists(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 增加值
     */
    public <T> Long incr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            return jedis.incr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 减少值
     */
    public <T> Long decr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            return jedis.decr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }


    public void sadd(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.sadd(key, field);
        } finally {
            returnToPool(jedis);
        }
    }


    public <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else if (clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else {
            return JSON.toJSONString(value);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    public void flush() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del("cheat:IP", "cheat:uid");
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * @param prefix uid 或者 IP
     * @param value  对应的ID或者IP地址
     * @return
     */
    public boolean verify(String prefix, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String cheatname = "cheat:" + prefix;
            if (jedis.sismember(cheatname, value))
                return false;
            Long current = System.currentTimeMillis() % propertiesConf.getRedisverifyTimes();

            String timename = "Time:" + prefix + ":" + current;
            String countname = "count:" + prefix;

            if (jedis.sismember(timename, value)) {
                Long count = jedis.hincrBy(countname, value, 1);
                if (count > propertiesConf.getRedisverifyNum()) {
                    jedis.sadd(cheatname, value);
                    return false;
                }
            } else {
                Pipeline pl = null;
                pl = jedis.pipelined();
                pl.sadd(timename, value);
                pl.expire(timename, propertiesConf.getRedisverifyTimes() * 2);
                pl.hset(countname, value, String.valueOf(0));
                pl.sync();
            }
            return true;
        } finally {
            returnToPool(jedis);
        }
    }
}
package com.doudizu.seckill.redis;

import com.alibaba.fastjson.JSON;
import com.doudizu.seckill.conf.PropertiesConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class RedisClusterService {
    @Autowired
    JedisCluster jedisCluster;
    @Autowired
    PropertiesConf propertiesConf;

    public <T> T get(KeyPrefix prefix, int key, Class<T> clazz) {
        try {
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            String str = jedisCluster.get(realKey);
            T t = stringToBean(str, clazz);
            return t;
        } catch (Exception ex)
        {

        }
        return null;
    }

    public <T> boolean set(KeyPrefix prefix, int key, T value) {
        try {
            String str = beanToString(value);
            if (str == null || str.length() <= 0) {
                return false;
            }
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            int seconds = prefix.expireSeconds();
            if (seconds <= 0) {
                jedisCluster.set(realKey, str);
            } else {
                jedisCluster.setex(realKey, seconds, str);
            }
            return true;
        } catch(Exception ex) {

        }
        return false;
    }

    public <T> boolean exists(KeyPrefix prefix, String key) {
        try {
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            return jedisCluster.exists(realKey);
        } catch (Exception x){

        }
        return false;
    }

    public <T> Long incr(KeyPrefix prefix, String key) {
        try {
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            return jedisCluster.incr(realKey);
        } catch (Exception ex){

        }
        return Long.MIN_VALUE;
    }

    public <T> Long decr(KeyPrefix prefix, String key) {
        try {
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            return jedisCluster.decr(realKey);
        } catch (Exception ex){
        }
        return Long.MAX_VALUE;
    }


    public boolean createpay(String uid,String pid)
    {
        try {
            String paypid="pay:pid:"+pid;
            String orderpid = "order:pid:"+pid;
            String payuid="pay:uid:"+uid;
            if(!jedisCluster.sismember(orderpid,uid) || jedisCluster.sismember(paypid,uid) ||
                    jedisCluster.scard(paypid)>=propertiesConf.getRedisclusterProductnum())
                return false;
            String lockkey = getlockkey("pay",pid);
            String lockvalue = getlockvalue(uid);
            if(!lock(lockkey,lockvalue))
                return false;
            if(jedisCluster.scard(paypid)>=propertiesConf.getRedisclusterProductnum())
                return false;
            jedisCluster.sadd(paypid,uid);
            jedisCluster.sadd(payuid,pid);
            releaselock(lockkey,lockvalue);
            jedisCluster.sadd("reset",paypid,payuid);
            return true;
        } catch (Exception ex){
        }
        return false;
    }
    public boolean createorder(String uid,String pid)
    {
        try {
            String orderpid="order:pid:"+pid;
            String orderuid="order:uid:"+uid;
            if(jedisCluster.sismember(orderpid,uid) || jedisCluster.scard(orderpid)>=propertiesConf.getRedisclusterProductnum())
                return false;
            String lockkey = getlockkey("order",pid);
            String lockvalue = getlockvalue(uid);
            if(!lock(lockkey,lockvalue))
                return false;
            if(jedisCluster.scard(orderpid)>=propertiesConf.getRedisclusterProductnum())
                return false;
            jedisCluster.sadd(orderpid,uid);
            jedisCluster.sadd(orderuid,pid);
            releaselock(lockkey,lockvalue);
            jedisCluster.sadd("reset",orderpid,orderuid);
            return true;
        } catch (Exception ex){

        }
        return false;
    }

    public void flush()
    {
        try{
            Set<String> keys = jedisCluster.smembers("reset");
            for(String key:keys)
            {
                jedisCluster.del(key);
            }
            jedisCluster.del("reset");
        }catch (Exception ex)
        {

        }
    }

    private String getlockkey(String prefix,String pid)
    {
        return "mutex:"+prefix+":"+pid;
    }

    private String getlockvalue(String uid)
    {
        int randno = UUID.randomUUID().toString().hashCode();
        if(randno<0)
            randno=-randno;
        return uid+String.format("%16d",randno);
    }
    private boolean lock(String key,String value)
    {
        try {
            for(int j=propertiesConf.getRedisclusterMutexnum();j>0;--j)
            {
                if(trylock(key,value,propertiesConf.getRedisclusterMutextime()))
                {
                    return true;
                }
            }
        } catch (Exception ex){
        }
        return false;
    }

    private boolean trylock(String key,String value,int expire)
    {
        try {
            String result = jedisCluster.set(key,value,"nx","ex",expire);
            return (result!=null) && ("OK".equals(result) || "+OK".equals(result));
        } catch (Exception ex){
        }
        return false;
    }

    private boolean releaselock(String key,String value)
    {
        try {
            if(value.equals(jedisCluster.get(key)))
            {
                jedisCluster.del(key);
                return true;
            }
            else
                return false;
        } catch (Exception ex){

        }
        return false;
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
}

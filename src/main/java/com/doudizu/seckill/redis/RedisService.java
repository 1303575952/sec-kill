package com.doudizu.seckill.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

@Service
public class RedisService {
	
	@Autowired
    JedisCluster jedisCluster;
	
	/**
	 * 获取单个对象
	 * */
	public <T> T get(KeyPrefix prefix, int key,  Class<T> clazz) {		 
		 
		 //生成真正的key
		 String realKey  = prefix.getPrefix() + key;
		 String  str = jedisCluster.get(realKey);
		 T t =  stringToBean(str, clazz);
		 return t;
		 
	}
	
	/**
	 * 设置对象
	 * */
	public <T> boolean set(KeyPrefix prefix, int key,  T value) {
		 String str = beanToString(value);
		 if(str == null || str.length() <= 0) {
			 return false;
		 }
		//生成真正的key
		 String realKey  = prefix.getPrefix() + key;
		 int seconds =  prefix.expireSeconds();
		 if(seconds <= 0) {
			 jedisCluster.set(realKey, str);
		 }else {
			 jedisCluster.setex(realKey, seconds, str);
		 }
		 return true;
	}
	
	/**
	 * 判断key是否存在
	 * */
	public <T> boolean exists(KeyPrefix prefix, String key) {		 
			//生成真正的key
			 String realKey  = prefix.getPrefix() + key;
			return  jedisCluster.exists(realKey);		 
	}
	
	/**
	 * 增加值
	 * */
	public <T> Long incr(KeyPrefix prefix, String key) {		 
			//生成真正的key
			 String realKey  = prefix.getPrefix() + key;
			return  jedisCluster.incr(realKey);		 
	}
	
	/**
	 * 减少值
	 * */
	public <T> Long decr(KeyPrefix prefix, String key) {
		//生成真正的key
		 String realKey  = prefix.getPrefix() + key;
		return  jedisCluster.decr(realKey);		 
	}

	/**
	 * 清空redis
	 */
	public void flush()
	{
		jedisCluster.flushAll();
	}

	/**
	 * 返回rediscluster实例
	 * @return
	 */
	public JedisCluster getJedisCluster()
	{
		return this.jedisCluster;
	}

	private <T> String beanToString(T value) {
		if(value == null) {
			return null;
		}
		Class<?> clazz = value.getClass();
		if(clazz == int.class || clazz == Integer.class) {
			 return ""+value;
		}else if(clazz == String.class) {
			 return (String)value;
		}else if(clazz == long.class || clazz == Long.class) {
			return ""+value;
		}else {
			return JSON.toJSONString(value);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T stringToBean(String str, Class<T> clazz) {
		if(str == null || str.length() <= 0 || clazz == null) {
			 return null;
		}
		if(clazz == int.class || clazz == Integer.class) {
			 return (T)Integer.valueOf(str);
		}else if(clazz == String.class) {
			 return (T)str;
		}else if(clazz == long.class || clazz == Long.class) {
			return  (T)Long.valueOf(str);
		}else {
			return JSON.toJavaObject(JSON.parseObject(str), clazz);
		}
	}
}

package com.doudizu.seckill.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisPoolFactory {

	@Autowired
	RedisConfig redisConfig;
	
	@Bean
	public JedisCluster JedisPoolFactory() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
		poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
		poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait() * 1000);
		
		Set<HostAndPort> nodes = new LinkedHashSet<HostAndPort>();
		
		nodes.add(new HostAndPort("10.108.18.83",7000));
		nodes.add(new HostAndPort("10.108.18.83",7001));
		nodes.add(new HostAndPort("10.108.18.84",7000));
		nodes.add(new HostAndPort("10.108.18.84",7001));
		nodes.add(new HostAndPort("10.108.18.85",7000));
		nodes.add(new HostAndPort("10.108.18.85",7001));
		nodes.add(new HostAndPort("10.108.18.86",7000));
		nodes.add(new HostAndPort("10.108.18.86",7001));
		
		JedisCluster cluster = new JedisCluster(nodes,poolConfig);		
		return cluster;
	}
	
}

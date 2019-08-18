package com.doudizu.seckill.service;

import com.doudizu.seckill.redis.JedisClusterPipeline;
import com.doudizu.seckill.redis.RedisService;
import redis.clients.jedis.JedisCluster;

import java.util.List;


class VerifyService {
	public boolean verifyByIp(RedisService redisService,String ip)
	{
		JedisCluster jediscluster=redisService.getJedisCluster();
		long currentTime = System.currentTimeMillis();
		//使用pipeline提高速度
		JedisClusterPipeline jcp = JedisClusterPipeline.pipelined(jediscluster);
		jcp.refreshCluster();
		List<Object> batchResult = null;
		try
		{

		}
		finally {
			jcp.close();
		}
		jediscluster.zremrangeByScore("ip:"+ip,0,currentTime-1000);
		long size = jediscluster.zcard("ip:"+ip);

		return true;
	}
}

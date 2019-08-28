package com.doudizu.seckill.redis;


import com.doudizu.seckill.conf.PropertiesConf;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

@Service
public class RedisClusterFactory {
    @Autowired
    PropertiesConf propertiesConf;

    @Bean
    public JedisCluster JedisClusterFactory()
    {
        Set<HostAndPort> nodes = new HashSet<>();

        nodes.add(new HostAndPort(propertiesConf.getRedisclusterAddress0(),propertiesConf.getRedisclusterPort1()));
        nodes.add(new HostAndPort(propertiesConf.getRedisclusterAddress1(),propertiesConf.getRedisclusterPort1()));
        nodes.add(new HostAndPort(propertiesConf.getRedisclusterAddress2(),propertiesConf.getRedisclusterPort1()));
        nodes.add(new HostAndPort(propertiesConf.getRedisclusterAddress3(),propertiesConf.getRedisclusterPort1()));
        nodes.add(new HostAndPort(propertiesConf.getRedisclusterAddress4(),propertiesConf.getRedisclusterPort1()));

        nodes.add(new HostAndPort(propertiesConf.getRedisclusterAddress0(),propertiesConf.getRedisclusterPort2()));
        nodes.add(new HostAndPort(propertiesConf.getRedisclusterAddress1(),propertiesConf.getRedisclusterPort2()));
        nodes.add(new HostAndPort(propertiesConf.getRedisclusterAddress2(),propertiesConf.getRedisclusterPort2()));
        nodes.add(new HostAndPort(propertiesConf.getRedisclusterAddress3(),propertiesConf.getRedisclusterPort2()));
        nodes.add(new HostAndPort(propertiesConf.getRedisclusterAddress4(),propertiesConf.getRedisclusterPort2()));

        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(1);
        genericObjectPoolConfig.setMaxTotal(16);
        genericObjectPoolConfig.setMaxWaitMillis(1000);

        JedisCluster jedisCluster = new JedisCluster(nodes,genericObjectPoolConfig);

        return jedisCluster;
    }
}

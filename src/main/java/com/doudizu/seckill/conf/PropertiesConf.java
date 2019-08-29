package com.doudizu.seckill.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class PropertiesConf {
    @Value("${sharding.product}")
    private int productTableCount;
    @Value("${pay.url}")
    private String payUrl;
    @Value("${pay.port}")
    private int payPort;
    @Value("${pay.path}")
    private String payPath;
    @Value("${reset.token}")
    private String resetToken;
    @Value("${product.category}")
    private int productCategory;

    @Value("${rediscluster.address0}")
    private String redisclusterAddress0;
    @Value("${rediscluster.address1}")
    private String redisclusterAddress1;
    @Value("${rediscluster.address2}")
    private String redisclusterAddress2;
    @Value("${rediscluster.address3}")
    private String redisclusterAddress3;
    @Value("${rediscluster.address4}")
    private String redisclusterAddress4;
    @Value("${rediscluster.port1}")
    private int redisclusterPort1;
    @Value("${rediscluster.port2}")
    private int redisclusterPort2;
    @Value("${redisverify.times}")
    private int redisverifyTimes;
    @Value("${redisverify.num}")
    private int redisverifyNum;
    @Value("${rediscluster.mutexnum}")
    private int redisclusterMutexnum;
    @Value("${rediscluster.mutextime}")
    private int redisclusterMutextime;
    @Value("${rediscluster.productnum}")
    private int redisclusterProductnum;
    @Value("${rediscluster.productlife}")
    private int redisclusterProductlife;
    @Value("${rediscluster.maxorder}")
    private int redisclusterMaxorder;
}

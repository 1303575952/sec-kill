package com.doudizu.seckill.conf;

import com.doudizu.seckill.domain.Product;
import com.doudizu.seckill.redis.ProductKey;
import com.doudizu.seckill.redis.RedisClusterService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Redis Init
 */

@Component
public class InitRedis implements InitializingBean {

    @Autowired
    RedisClusterService redisService;

    @Override
    public void afterPropertiesSet() throws Exception {

        /*这里调用需要配合初始化的方法*/
        /*cache */
        /**
         * 从数据库中读数据加载到redis中 读多少
         */
        Product product = new Product();
        product.setPid(1);
        product.setDetail("this is 176467546 detail");
        redisService.set(ProductKey.getByPid, 176467546, product);//pid

        System.out.println("项目启动初始化时会执行");
    }

}

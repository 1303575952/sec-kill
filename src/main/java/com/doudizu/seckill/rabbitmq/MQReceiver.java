package com.doudizu.seckill.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.doudizu.seckill.domain.Order;
import com.doudizu.seckill.domain.Product;
import com.doudizu.seckill.redis.RedisService;
import com.doudizu.seckill.service.OrderService;
import com.doudizu.seckill.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by smh on 2019/8/14.
 */
@Service
public class MQReceiver {

    @Autowired
    ProductService productService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void receive(String message) {
        Map<String, Object> returnMap = new HashMap<>();
        log.info("receive message:" + message);
        SeckillMessage seckillMessage = RedisService.stringToBean(message, SeckillMessage.class);
        int pid=seckillMessage.getPid();
        int uid=seckillMessage.getUid();
        String orderId=seckillMessage.getOrderId();

        Product product = productService.getProductByPid(pid);
        int stock = product.getCount();
        if (stock <= 0) {
            log.info("商品" + pid + "库存不够，不可下单");
            returnMap.put("code", 1);
            return ;
        }
        //判断是否可秒杀（已购买过）
        List<Order> orders = orderService.getOrderByUidAndPid(uid, pid);
        if (orders.size() >= 1) {
            log.info("商品" + pid + "用户" + uid + "已经购买过");
            returnMap.put("code", 1);
            log.info(JSONObject.toJSON(returnMap).toString());
            return ;
        }

        //减库存 下订单 写入秒杀订单
        String orderId2 = orderService.createOrder(uid, pid);
    }


//    @RabbitListener(queues = MQConfig.QUEUE)
//    public void receiver(String message){
//        log.info("recevier"+message);
//    }

//    @RabbitListener(queues = MQConfig.QUEUE)
//    public void receive(String message) {
//        log.info("receive message:" + message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
//    public void receiveTopic1(String message) {
//        log.info("topic1 message:" + message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
//    public void receiveTopic2(String message) {
//        log.info("topic2 message:" + message);
//    }
//
//    @RabbitListener(queues = MQConfig.HEADER_QUEUE)
//    public void receiveHeader(byte[] message) {
//        log.info("headers queue message:" + new String(message));
//    }
}

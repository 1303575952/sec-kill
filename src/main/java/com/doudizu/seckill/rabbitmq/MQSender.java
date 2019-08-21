package com.doudizu.seckill.rabbitmq;

import com.doudizu.seckill.redis.RedisPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by smh on 2019/8/14.
 */
@Service
public class MQSender {
    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    RedisPoolService redisService;
    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    public void sendSeckillMessage(Object message){
        //将bean转为字符串
        String msg = redisService.beanToString(message);
        //发送
        amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE,msg);
        log.info("send:"+msg);

    }
//    public void send(Object message) {
//        String msg = RedisService.beanToString(message);
//        log.info("send message:" + msg);
//        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
//    }
//
//    public void sendTopic(Object message) {
//        String msg = RedisService.beanToString(message);
//        log.info("send message:" + msg);
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg + "1");
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg + "2");
//    }
//
//    public void sendFanout(Object message) {
//        String msg = RedisService.beanToString(message);
//        log.info("send fanout message:" + msg);
//        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg);
//    }
//
//    public void sendHeaders(Object message) {
//        String msg = RedisService.beanToString(message);
//        log.info("send fanout message:" + msg);
//        MessageProperties messageProperties = new MessageProperties();
//        messageProperties.setHeader("header1", "value1");
//        messageProperties.setHeader("header2", "value2");
//        Message obj = new Message(msg.getBytes(), messageProperties);
//        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", obj);
//    }
//
}

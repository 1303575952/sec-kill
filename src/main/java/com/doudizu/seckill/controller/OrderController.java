package com.doudizu.seckill.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doudizu.seckill.conf.PropertiesConf;
import com.doudizu.seckill.domain.Order;
import com.doudizu.seckill.domain.Product;
import com.doudizu.seckill.redis.RedisService;
import com.doudizu.seckill.service.OrderService;
import com.doudizu.seckill.service.ProductService;
import com.doudizu.seckill.util.HttpClient;
import com.doudizu.seckill.util.JsonGenerate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class OrderController {
    @Autowired
    PropertiesConf propertiesConf;

    @Autowired
    ProductService productService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    //全部订单接口
    @RequestMapping("/result")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOrdersByUid(@RequestParam("uid") int uid) {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("data", orderService.getOrdersByUid(uid));
        return new ResponseEntity<>(returnMap, HttpStatus.OK);
    }

    //下订单接口
    @RequestMapping(value = "/order", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map> createOrder(@RequestBody Map<String, String> map) {
        Map<String, Object> returnMap = new HashMap<>();
        int uid = Integer.valueOf(map.get("uid"));
        int pid = Integer.valueOf(map.get("pid"));
        //判断库存
        Product product = productService.getProductByPid(pid);
        int stock = product.getCount();
        if (stock <= 0) {
            returnMap.put("code", 1);
            return new ResponseEntity<>(returnMap, HttpStatus.OK);
        }
        //判断是否可秒杀（已购买过）
        List<Order> orders = orderService.getOrderByUidAndPid(uid, pid);
        if (orders.size() >= 1) {
            returnMap.put("code", 1);
            return new ResponseEntity<>(returnMap, HttpStatus.OK);
        }
        //减库存 下订单 写入秒杀订单
        String orderId = orderService.createOrder(uid, pid);
        log.info("orderId:" + orderId);
        returnMap.put("code", 0);
        returnMap.put("order_id", orderId);
        return new ResponseEntity<>(returnMap, HttpStatus.OK);
    }

    //支付接口
    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map> payOrder(@RequestBody Map<String, String> map) {
        log.info(map.toString());
        Map<String, Object> returnMap = new HashMap<>();
        int uid = Integer.valueOf(map.get("uid"));
        int price = Integer.valueOf(map.get("price"));
        String orderId = map.get("order_id");
        String url = propertiesConf.getPayUrl() + ":" + propertiesConf.getPayPort() + propertiesConf.getPayPath();
        JSONObject json = JsonGenerate.generatePayJsonString(uid, price, orderId);
        log.info(url);
        log.info(json.toString());
        String tokenJsonStr = HttpClient.httpPostWithJSON(url, json.toString());
        log.info(tokenJsonStr);
        JSONObject tokenJson = JSON.parseObject(tokenJsonStr);
        String token = (String) tokenJson.get("token");
        int code = orderService.payOrder(token, uid, price, orderId);
        log.info("code:" + code);
        log.info("token:" + token);
        if (code == 0) {
            returnMap.put("code", code);
            returnMap.put("token", token);
        } else {
            returnMap.put("code", code);
        }
        log.info(JSON.toJSONString(returnMap));
        return new ResponseEntity<>(returnMap, HttpStatus.OK);
    }
}

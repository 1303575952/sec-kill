package com.doudizu.seckill.controller;

import com.doudizu.seckill.domain.Order;
import com.doudizu.seckill.domain.Product;
import com.doudizu.seckill.redis.RedisService;
import com.doudizu.seckill.service.OrderService;
import com.doudizu.seckill.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Autowired
    RedisService redisService;

    @Autowired
    ProductService productService;

    @Autowired
    OrderService orderService;

    //一个用户的所有订单，不做压测
    @RequestMapping("/result")
    @ResponseBody
    public Map<String, Object> getOrdersByUid(@RequestParam("uid") int uid) {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("data", orderService.getOrdersByUid(uid));
        return returnMap;
    }

    //下单
    @RequestMapping(value = "/order", method = RequestMethod.POST)
    @ResponseBody
    public Map createOrder(@RequestBody Map<String, String> map) {
        Map<String, Object> returnMap = new HashMap<>();
        int uid = Integer.valueOf(map.get("uid"));
        int pid = Integer.valueOf(map.get("pid"));
        //判断库存
        Product product = productService.getProductByPid(pid);
        int stock = product.getCount();
        if (stock <= 0) {
            returnMap.put("code", 1);
            return returnMap;
        }
        //判断是否可秒杀
        List<Order> orders = orderService.getOrderByUidAndPid(uid, pid);
        if (orders.size() >= 1) {
            //TODO 已经下订单了，重复购买
        }
        //减库存 下订单 写入秒杀订单
        String orderId = orderService.createOrder(uid, pid);
        returnMap.put("code", 0);
        returnMap.put("orderId", orderId);
        return returnMap;
    }

    //支付订单
    @RequestMapping("/pay")
    @ResponseBody
    public Map payOrder(@RequestBody Map<String, String> map) {
        Map<String, Object> returnMap = new HashMap<>();
        int uid = Integer.valueOf(map.get("uid"));
        int price = Integer.valueOf(map.get("price"));
        String orderId = map.get("orderId");
        int code = orderService.payOrder(uid, price, orderId);
        returnMap.put("code", code);
        returnMap.put("token", "");
        return returnMap;
    }
}

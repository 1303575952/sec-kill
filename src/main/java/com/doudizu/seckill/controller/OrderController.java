package com.doudizu.seckill.controller;

import com.doudizu.seckill.domain.Order;
import com.doudizu.seckill.domain.Product;
import com.doudizu.seckill.redis.RedisService;
import com.doudizu.seckill.service.OrderService;
import com.doudizu.seckill.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    RedisService redisService;

    @Autowired
    ProductService productService;

    @Autowired
    OrderService orderService;

    //下单
    @RequestMapping("/order")
    @ResponseBody
    public String createOrder() {
        int uid = 123;
        int pid = 176467513;
        //判断库存
        Product product = productService.getProductByPid(pid);
        int stock = product.getCount();
        if (stock <= 0) {
            return "stock fail";
        }
        //判断是否可秒杀
        List<Order> orders = orderService.getOrderByUidAndPid(uid, pid);
        if (orders.size() >=1) {
            return "seckill fail";
        }
        //减库存 下订单 写入秒杀订单
        String orderId = orderService.createOrder(uid, pid);
        return orderId;
    }
}

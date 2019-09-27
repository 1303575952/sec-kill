package com.doudizu.seckill.controller;

import com.doudizu.seckill.conf.PropertiesConf;
import com.doudizu.seckill.domain.Product;
import com.doudizu.seckill.redis.RedisClusterService;
import com.doudizu.seckill.redis.RedisService;
import com.doudizu.seckill.service.OrderService;
import com.doudizu.seckill.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class ProductController {
    @Autowired
    PropertiesConf propertiesConf;
    @Autowired
    ProductService productService;
    @Autowired
    OrderService orderService;
    @Autowired
    RedisService redisService;
    @Autowired
    RedisClusterService redisClusterService;

    @GetMapping("/productTest")
    @ResponseBody
    public String getProdutTest() {
        return "product detail";
    }

    //商品信息接口
    @GetMapping("/product")
    @ResponseBody
    public ResponseEntity getProduct(@RequestParam("pid") long pid) {
        String res = redisClusterService.getproduct(String.valueOf(pid));
        if (res == null) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } else {
            Product product = new Product();
            String[] strArr = res.split("-");
            product.setPid(pid);
            product.setCount(Integer.valueOf(strArr[0]));
            product.setPrice(Integer.valueOf(strArr[1]));
            product.setDetail(strArr[2]);
            return new ResponseEntity<>(product, HttpStatus.OK);
        }
    }

    //状态复原接口接口
    @PostMapping("/reset")
    @ResponseBody
    public ResponseEntity<Map> reset(@RequestBody Map<String, String> map) {
        String token = map.get("token");
        Map<String, Object> returnMap = new HashMap<>();
        int code;
        if (!token.equals(propertiesConf.getResetToken())) {
            returnMap.put("code", 1);
            return new ResponseEntity<>(returnMap, HttpStatus.OK);
        }
        //log.info("开始reset");
        redisClusterService.flush();
        //log.info("集群reset完毕");
        redisClusterService.flushcheat();
        returnMap.put("code", 0);
        return new ResponseEntity<>(returnMap, HttpStatus.OK);
    }
}

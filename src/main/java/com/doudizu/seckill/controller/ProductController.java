package com.doudizu.seckill.controller;

import com.doudizu.seckill.conf.PropertiesConf;
import com.doudizu.seckill.domain.Product;
import com.doudizu.seckill.redis.ProductKey;
import com.doudizu.seckill.redis.RedisService;
import com.doudizu.seckill.result.Result;
import com.doudizu.seckill.service.OrderService;
import com.doudizu.seckill.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @GetMapping("/productTest")
    @ResponseBody
    public String getProdutTest() {
        return "product detail";
    }

    @GetMapping("/product/mysql")
    @ResponseBody
    public Product getProduct(@RequestParam("pid") int pid) {
        Product product = productService.getProductByPid(pid);
        return product;
    }

    @RequestMapping("/product/redis")
    @ResponseBody
    public Product redisGet(@RequestParam("pid") int pid) {
        Product product = redisService.get(ProductKey.getByPid, pid, Product.class);
        return product;
    }

    @RequestMapping("/product/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet(@RequestParam("pid") int pid) {
        Product product = new Product();
        product.setPid(1);
        product.setDetail("this is detail");
        redisService.set(ProductKey.getByPid, pid, product);//pid
        return Result.success(true);
    }

    //重置接口
    @RequestMapping("/reset")
    @ResponseBody
    public Map reset(@RequestParam("token") String token) {
        Map<String, Object> returnMap = new HashMap<>();
        int code;
        if (!token.equals(propertiesConf.getResetToken())) {
            returnMap.put("code", 1);
            return returnMap;
        }
        int p = productService.resetProduct();
        int o = orderService.clearOrder();
        log.info("p:" + p + ", o:" + o);
        if (p == propertiesConf.getProductCategory() && o == 0) {
            code = 0;
        } else {
            code = 1;
        }
        returnMap.put("code", code);
        return returnMap;
    }
}

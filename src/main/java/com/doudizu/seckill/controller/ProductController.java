package com.doudizu.seckill.controller;

import com.doudizu.seckill.domain.Product;
import com.doudizu.seckill.redis.ProductKey;
import com.doudizu.seckill.redis.RedisService;
import com.doudizu.seckill.result.Result;
import com.doudizu.seckill.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProductController {
    @Autowired
    ProductService productService;
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
}

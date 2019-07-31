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

    @GetMapping("/product")
    @ResponseBody
    public Result<Product> getProduct() {
        Product product = productService.getProductByPid(176467513);
        return Result.success(product);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<Product> redisGet() {
        Product product = redisService.get(ProductKey.getByPid, "" + 1, Product.class);
        return Result.success(product);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
        Product product = new Product();
        product.setPid(1);
        product.setDetail("this is detail");
        redisService.set(ProductKey.getByPid, "" + 1, product);//ProductKey:pid1
        return Result.success(true);
    }
}

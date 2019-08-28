package com.doudizu.seckill.service;

import com.doudizu.seckill.conf.PropertiesConf;
import com.doudizu.seckill.dao.ProductDao;
import com.doudizu.seckill.domain.Product;
import com.doudizu.seckill.util.ProductTableNum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Slf4j
@Service
public class ProductService {

    @Autowired
    ProductDao productDao;
    @Autowired
    PropertiesConf propertiesConf;

    //通过商品编号获取商品消息
    /*public Product getProductByPid(int pid) {
        log.debug("product_" + pid % propertiesConf.getProductTableCount());
        return productDao.getProductByPid("product_" + pid % propertiesConf.getProductTableCount(), pid);
    }*/
    public Product getProductByPid(long pid) {
        log.info("product_" + ProductTableNum.getProductTableNum(pid));
        return productDao.getProductByPid("product_" + ProductTableNum.getProductTableNum(pid), pid);
    }

    //根据商品编号减商品库存
    public void reduceStock(long pid) {
        productDao.reduceStock(pid);
    }

    //恢复商品原始数量100，返回数据为商品总数量
    public int resetProduct() {
        return productDao.resetProduct();
    }
}

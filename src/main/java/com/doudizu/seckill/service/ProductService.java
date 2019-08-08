package com.doudizu.seckill.service;

import com.doudizu.seckill.dao.ProductDao;
import com.doudizu.seckill.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    ProductDao productDao;

    //通过商品编号获取商品消息
    public Product getProductByPid(int pid) {
        return productDao.getProductByPid(pid);
    }

    //根据商品编号减商品库存
    public void reduceStock(int pid) {
        productDao.reduceStock(pid);
    }

    //恢复商品原始数量100，返回数据为商品总数量
    public int resetProduct() {
        return productDao.resetProduct();
    }
}

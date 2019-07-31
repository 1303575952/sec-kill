package com.doudizu.seckill.service;

import com.doudizu.seckill.dao.ProductDao;
import com.doudizu.seckill.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    ProductDao productDao;

    public Product getProductByPid(int pid) {
        return productDao.getProductByPid(pid);
    }

    public void reduceStock(int pid) {
        productDao.reduceStock(pid);
    }
}

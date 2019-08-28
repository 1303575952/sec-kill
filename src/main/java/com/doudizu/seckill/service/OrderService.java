package com.doudizu.seckill.service;

import com.doudizu.seckill.dao.OrderDao;
import com.doudizu.seckill.domain.Order;
import com.doudizu.seckill.domain.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    OrderDao orderDao;
    @Autowired
    ProductService productService;

    //一个用户的所有订单
    public List<OrderInfo> getOrdersByUid(int uid) {
        return orderDao.getOrdersByUid(uid);
    }

    //某用户某商品的订单
    public List<Order> getOrderByUidAndPid(int uid, long pid) {
        return orderDao.getOrderByUidAndPid(uid, pid);
    }

    //先减库存再下订单
    @Transactional
    public String createOrder(int uid, long pid) {
        productService.reduceStock(pid);
        String orderId = "" + System.currentTimeMillis() + pid + uid;
        orderDao.createOrder(orderId, uid, pid);
        return orderId;
    }

    //支付。0表示成功，1表示失败
    public int payOrder(String token, int uid, int price, String orderId) {
        return orderDao.updatePayStatusAndToken(token, uid, price, orderId) == 1 ? 0 : 1;
    }

    //清空订单表，0表示清空
    public int clearOrder() {
        return orderDao.clearOrder();
    }
}

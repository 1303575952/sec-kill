package com.doudizu.seckill.service;

import com.doudizu.seckill.dao.OrderDao;
import com.doudizu.seckill.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    OrderDao orderDao;
    @Autowired
    ProductService productService;

    //一个用户的所有订单
    public List<Order> getOrdersByUid(int uid) {
        return orderDao.getOrdersByUid(uid);
    }

    //某用户某商品的订单
    public List<Order> getOrderByUidAndPid(int uid, int pid) {
        return orderDao.getOrderByUidAndPid(uid, pid);
    }

    //先减库存再下订单
    @Transactional
    public String createOrder(int uid, int pid) {
        productService.reduceStock(pid);
        Order order = new Order();
        String orderId = "" + System.currentTimeMillis() + pid + uid;
        orderDao.createOrder(orderId, uid, pid);
        return order.getOrderId();
    }

    //支付
    public int payOrder(int uid, int price, String orderId) {
        return orderDao.updatePayStatus(uid, price, orderId);
    }
}

package com.doudizu.seckill.redis;

public class OrderKey extends BasePrefix {

    private OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getByOrderId = new OrderKey("");//暂时不用前缀，设为空串
}

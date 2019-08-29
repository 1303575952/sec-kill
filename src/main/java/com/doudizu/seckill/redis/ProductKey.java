package com.doudizu.seckill.redis;

public class ProductKey extends BasePrefix {

    private ProductKey(String prefix) {
        super(prefix);
    }

    public static ProductKey getByPid = new ProductKey("");//暂时不用前缀，设为空串
}

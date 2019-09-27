package com.doudizu.seckill.redis;

public interface KeyPrefix {

    int expireSeconds();

    String getPrefix();

}

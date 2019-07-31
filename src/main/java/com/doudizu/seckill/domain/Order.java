package com.doudizu.seckill.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order {
    private String orderId;
    private int uid;
    private int pid;
    private int status;
    private String token;
}

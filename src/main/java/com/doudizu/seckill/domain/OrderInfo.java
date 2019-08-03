package com.doudizu.seckill.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderInfo {
    private int uid;
    private int pid;
    private String detail;
    private String orderId;
    private int price;
    private int status;
    private String token;
}

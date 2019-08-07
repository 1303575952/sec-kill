package com.doudizu.seckill.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {
    private int pid;
    private int price;
    private String detail;
    private int count;
}

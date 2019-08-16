package com.doudizu.seckill.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order {
    @JsonProperty("order_id")
    private String orderId;
    private int uid;
    private int pid;
    private int status;
    private String token;
}

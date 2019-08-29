package com.doudizu.seckill.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderInfo implements Comparable<OrderInfo> {
    private long uid;
    private long pid;
    private String detail;
    @JsonProperty("order_id")
    private String orderId;
    private int price;
    private int status;
    private String token;

    @Override
    public int compareTo(OrderInfo o) {
        return (int) (this.getPid() - o.getPid());
    }
}

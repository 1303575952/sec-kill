package com.doudizu.seckill.rabbitmq;

/**
 * Created by smh on 2019/8/19.
 */
public class SeckillMessage {
//    private SeckillUser seckillUser;
//    private long goodsId;
//
//    @Override
//    public String toString() {
//        return "SeckillMessage{" +
//                "seckillUser=" + seckillUser +
//                ", goodsId=" + goodsId +
//                '}';
//    }
    private int uid;
    private int pid;
    private String orderId;// = "" + System.currentTimeMillis() + pid + uid;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}

package com.doudizu.seckill.util;

import com.alibaba.fastjson.JSONObject;

public class JsonGenerate {
    public static JSONObject generatePayJsonString(int uid, int price, String orderId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uid", uid);
        jsonObject.put("price", price);
        jsonObject.put("order_id", orderId);
        return jsonObject;
    }

    public static void main(String[] args) {
        System.out.println(generatePayJsonString(11111, 82, "1000110001"));
    }
}

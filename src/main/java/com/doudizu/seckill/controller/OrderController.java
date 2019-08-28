package com.doudizu.seckill.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doudizu.seckill.conf.PropertiesConf;
import com.doudizu.seckill.domain.Order;
import com.doudizu.seckill.domain.OrderInfo;
import com.doudizu.seckill.domain.Product;
import com.doudizu.seckill.redis.OrderKey;
import com.doudizu.seckill.redis.RedisClusterService;
import com.doudizu.seckill.redis.RedisService;
import com.doudizu.seckill.service.OrderService;
import com.doudizu.seckill.service.ProductService;
import com.doudizu.seckill.util.HttpClient;
import com.doudizu.seckill.util.JsonGenerate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.*;

@Slf4j
@Controller
public class OrderController {
    @Autowired
    PropertiesConf propertiesConf;

    @Autowired
    ProductService productService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Autowired
    RedisClusterService redisClusterService;

    //全部订单接口
    @RequestMapping("/result")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOrdersByUid(HttpServletRequest request, @RequestParam("uid") long uid) {
        String uidStr = String.valueOf(uid);
        Map<String, Object> returnMap = new HashMap<>();
        log.info(request.getQueryString());
        String sessionid = request.getHeader("sessionid");
        log.info("sessionid:" + sessionid);
        //拿到redis上sessionid对应的uid
        long requestUid = Long.valueOf(redisService.getKey(OrderKey.getByOrderId, sessionid));
        String ip = request.getHeader("X-Forwarded-For");
        log.info("requestUid:" + requestUid + " ip:" + ip);

        String[] arr = redisClusterService.getallorder(uidStr);
        log.info("arr.lenth()" + arr.length);
        /*for (int i = 0; i < arr.length; i++) {
            log.info(arr[i]);
        }*/
        //pid-price-detail-status-order_id-token
        log.info("新建orderinfo");
        OrderInfo[] orderInfos = new OrderInfo[arr.length];
        log.info("建立完毕");
        for (int i = 0; i < arr.length; i++) {
            String[] strArr = arr[i].split("-");
            log.info("strArr.length:" + strArr.length);
            orderInfos[i] = new OrderInfo();
            orderInfos[i].setUid(uid);
            orderInfos[i].setPid(Long.valueOf(strArr[0]));
            orderInfos[i].setPrice(Integer.valueOf(strArr[1]));
            orderInfos[i].setDetail(strArr[2]);
            orderInfos[i].setStatus(Integer.valueOf(strArr[3]));
            orderInfos[i].setOrderId(strArr[4]);
            if (strArr.length == 6) {
                orderInfos[i].setToken(strArr[5]);
            } else {
                orderInfos[i].setToken("");
            }
        }
        log.info("开始排序");
        Arrays.sort(orderInfos);
        log.info("排序完成");
        List<OrderInfo> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            list.add(orderInfos[i]);
            log.info("array:" + i + list.get(i).toString());
        }
        returnMap.put("data", list);
        //returnMap.put("data", orderService.getOrdersByUid(uid));
        return new ResponseEntity<>(returnMap, HttpStatus.OK);
    }


    //订单接口
    @RequestMapping(value = "/order", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map> createOrder(HttpServletRequest request, @RequestBody Map<String, String> map) {
        Map<String, Object> returnMap = new HashMap<>();
        log.info(request.getQueryString());
        String uid = map.get("uid");
        String pidStr = map.get("pid");
        String sessionid = request.getHeader("sessionid");
        log.info("sessionid:" + sessionid);
        //拿到redis上sessionid对应的uid
        long requestUid = Long.valueOf(redisService.getKey(OrderKey.getByOrderId, sessionid));
        String ip = request.getHeader("X-Forwarded-For");
        log.info("requestUid:" + requestUid + " ip:" + ip);

        String order_id = uid + "~" + pidStr + "~" + System.currentTimeMillis() / 1000;

        //判断请求的uid和参数中uid是否一致,ip黑名单
        //请求的uid和参数中uid不一致,ip黑名单

        if (!redisService.verifyall(uid, sessionid, ip) || !redisClusterService.verify(uid)) {
            log.info("作弊用户" + "uid:" + uid + " ip:" + ip + " sessionid:" + sessionid);
            redisService.sadd("cheat:IP", ip);
            redisService.sadd("cheat:uid", uid);
            return new ResponseEntity<>(returnMap, HttpStatus.FORBIDDEN);
        }
        if (redisClusterService.createorder(uid, pidStr, order_id)) {
            returnMap.put("code", 0);
            returnMap.put("order_id", order_id);
            log.info("下单成功" + order_id);
        } else {
            returnMap.put("code", 1);
            log.info("下单失败" + order_id);
        }
        return new ResponseEntity<>(returnMap, HttpStatus.OK);
    }

    //支付接口
    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map> payOrder(HttpServletRequest request, @RequestBody Map<String, String> map) {
        Map<String, Object> returnMap = new HashMap<>();
        log.info(request.getQueryString());
        String uid = map.get("uid");
        String orderId = map.get("order_id");
        int price = Integer.valueOf(map.get("price"));
        String sessionid = request.getHeader("sessionid");
        log.info("sessionid:" + sessionid);
        //拿到redis上sessionid对应的uid
        long requestUid = Long.valueOf(redisService.getKey(OrderKey.getByOrderId, sessionid));
        String ip = request.getHeader("X-Forwarded-For");
        log.info("requestUid:" + requestUid + " ip:" + ip);

        //判断请求的uid和参数中uid是否一致,ip黑名单
        //请求的uid和参数中uid不一致,ip黑名单

        if (!redisService.verifyall(uid, sessionid, ip)) {
            log.info("作弊用户" + "uid:" + uid + " ip:" + ip + " sessionid:" + sessionid);
            return new ResponseEntity<>(returnMap, HttpStatus.FORBIDDEN);
        }
        String[] strOrderArr = orderId.split("~");
        String pidStr = strOrderArr[1];
        String str = redisClusterService.getproduct(pidStr);
        if (str == null) {
            redisService.sadd("cheat:IP", ip);
            redisService.sadd("cheat:uid", uid);

            return new ResponseEntity<>(returnMap, HttpStatus.FORBIDDEN);
        }

        String[] strArr = str.split("-");
        int realPrice = Integer.valueOf(strArr[1]);
        if (price != realPrice) {
            redisService.sadd("cheat:IP", ip);
            redisService.sadd("cheat:uid", uid);

            return new ResponseEntity<>(returnMap, HttpStatus.FORBIDDEN);
        }
        int count = Integer.valueOf(strArr[0]);
        if (count <= 0) {
            returnMap.put("code", 1);
            return new ResponseEntity<>(returnMap, HttpStatus.OK);
        }

        //通过下面url获取token
        String url = propertiesConf.getPayUrl() + ":" + propertiesConf.getPayPort() + propertiesConf.getPayPath();
        JSONObject json = JsonGenerate.generatePayJsonString(Long.valueOf(uid), price, orderId);
        log.info(url);
        log.info(json.toString());
        String tokenJsonStr = HttpClient.httpPostWithJSON(url, json.toString());
        log.info(tokenJsonStr);
        JSONObject tokenJson = JSON.parseObject(tokenJsonStr);
        String token = (String) tokenJson.get("token");
        if (redisClusterService.createpay(uid, pidStr, token)) {
            returnMap.put("code", 0);
            returnMap.put("token", token);
            return new ResponseEntity<>(returnMap, HttpStatus.OK);
        } else {
            redisService.sadd("cheat:IP", ip);
            redisService.sadd("cheat:uid", uid);
            returnMap.put("code", 1);
            return new ResponseEntity<>(returnMap, HttpStatus.FORBIDDEN);
        }

    }
}

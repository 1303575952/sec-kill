package com.doudizu.seckill.dao;

import com.doudizu.seckill.domain.Order;
import com.doudizu.seckill.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface OrderDao {
    //插入订单
    @Insert("insert into `order` (order_id,uid,pid,price) values(#{orderId},#{uid},#{pid},(select price from product where pid=#{pid}))")
    int createOrder(@Param("orderId") String orderId, @Param("uid") int uid, @Param("pid") long pid);

    //支付，修改status状态
    @Update("update `order` set status=1 where uid=#{uid} and price=#{price} and order_id=#{orderId}")
    int updatePayStatus(@Param("uid") int uid, @Param("price") int price, @Param("orderId") String orderId);

    //支付，修改status状态和token
    @Update("update `order` set status=1, token=#{token} where uid=#{uid} and price=#{price} and order_id=#{orderId}")
    int updatePayStatusAndToken(@Param("token") String token, @Param("uid") int uid, @Param("price") int price, @Param("orderId") String orderId);

    //查询某用户的全部订单
    @Select("select A.uid,A.pid,B.detail,A.order_id,A.price,A.status,A.token from (select * from `order` where uid=#{uid}) as A left join product as B on A.pid=B.pid order by A.pid")
    List<OrderInfo> getOrdersByUid(@Param("uid") int uid);

    //查询某用户某商品的订单
    @Select("select * from `order` where uid=#{uid} and pid=#{pid}")
    List<Order> getOrderByUidAndPid(@Param("uid") int uid, @Param("pid") long pid);

    //清空订单表
    @Update("truncate table `order`")
    int clearOrder();
}

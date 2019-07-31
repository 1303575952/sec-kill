package com.doudizu.seckill.dao;

import com.doudizu.seckill.domain.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderDao {
    //插入订单
    @Insert("insert into `order` (order_id,uid,pid) values(#{orderId},#{uid},#{pid})")
    int createOrder(@Param("orderId") String orderId, @Param("uid") int uid, @Param("pid") int pid);

    //支付，修改status状态
    @Update("update `order` set status=1 where order_id=#{orderId}")
    int updatePayStatus(@Param("orderId") String orderId);

    //查询某用户的全部订单
    @Select("select * from `order` where uid=#{uid}")
    List<Order> getOrdersByUid(@Param("uid") int uid);

    //查询某用户某商品的订单
    @Select("select * from `order` where uid=#{uid} and pid=#{pid}")
    List<Order> getOrderByUidAndPid(@Param("uid") int uid, @Param("pid") int pid);
}

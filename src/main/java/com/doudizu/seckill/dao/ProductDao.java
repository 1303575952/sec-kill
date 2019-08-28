package com.doudizu.seckill.dao;

import com.doudizu.seckill.domain.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigInteger;

@Mapper
public interface ProductDao {
    //通过商品编号查询商品详情
    @Select("select * from ${productTable} where pid=#{pid}")
    Product getProductByPid(@Param("productTable") String productTable, @Param("pid") long pid);

    //减库存
    @Update("update product set count = count - 1 where pid = #{pid}")
    int reduceStock(@Param("pid") long pid);

    //恢复商品原始数量100
    @Update("update product set count=100")
    int resetProduct();
}

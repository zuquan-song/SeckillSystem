package org.seckill.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.seckill.domain.SeckillOrder;
import org.seckill.domain.OrderInfo;

/**
 * @author Zuquan Song
 *
 * @description OrderMapper
 */
public interface OrderMapper {

    @Select("SELECT * FROM miaosha_order WHERE user_id = #{userId} AND goods_id = #{goodsId} ")
    SeckillOrder getMiaoshaOrderByUserIdGoodsId(@Param("userId")Long userId, @Param("goodsId")Long goodsId);

    @Insert("INSERT INTO order_info(user_id, goods_id, goods_name, goods_price, goods_count, order_channel, status, create_date) " +
            "VALUES (#{userId}, #{goodsId}, #{goodsName}, #{goodsPrice}, #{goodsCount}, #{orderChannel}, #{status}, #{createDate} )")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "select last_insert_id()")
    long insert(OrderInfo orderInfo);

    @Insert("INSERT INTO miaosha_order (user_id, order_id, goods_id) VALUES (#{userId}, #{orderId}, #{goodsId});")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "select last_insert_id()")
    long insertMiaoshaOrder(SeckillOrder seckillOrder);

    @Select("SELECT * FROM miaosha_order WHERE id = #{orderId} ")
    OrderInfo getById(long orderId);
}

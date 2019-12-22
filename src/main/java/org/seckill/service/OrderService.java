package org.seckill.service;

import lombok.extern.slf4j.Slf4j;
import org.seckill.domain.SeckillUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.seckill.common.redis.OrderKey;
import org.seckill.domain.SeckillOrder;
import org.seckill.domain.OrderInfo;
import org.seckill.mapper.OrderMapper;
import org.seckill.vo.GoodsVo;

import java.time.Instant;
import java.util.Date;

/**
 * @author Zuquan Song
 *
 * @description OrderService
 */
@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RedisService redisService;

    public SeckillOrder getMiaoshaOrderByUserIdGoodsId(Long userId, Long goodsId) {

        return redisService.get(OrderKey.getMiaoshaOrderByUidGid, ""+userId+"_"+goodsId, SeckillOrder.class);
    }

    public OrderInfo createOrder(SeckillUser user, GoodsVo goodsVo) {

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(Date.from(Instant.now()));
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());

        orderMapper.insert(orderInfo);

        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(user.getId());

        orderMapper.insertMiaoshaOrder(seckillOrder);

        redisService.set(OrderKey.getMiaoshaOrderByUidGid, ""+user.getId()+"_"+goodsVo.getId(), seckillOrder);

        log.info("Order Successfully. userId={}, goodsId={}", user.getId(), goodsVo.getId());
        return orderInfo;
    }

    public OrderInfo getById(long orderId) {
        return orderMapper.getById(orderId);
    }
}

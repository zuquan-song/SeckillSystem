package org.seckill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.seckill.domain.SeckillUser;
import org.seckill.domain.OrderInfo;
import org.seckill.result.CodeMsg;
import org.seckill.result.Result;
import org.seckill.service.GoodsService;
import org.seckill.service.OrderService;
import org.seckill.vo.GoodsVo;
import org.seckill.vo.OrderDetailVo;

/**
 * @author Zuquan Song
 *
 * @description OrderController
 */
@RequestMapping("order")
@Controller
public class  OrderController {

    private final OrderService orderService;
    private final GoodsService goodsService;

    @Autowired
    public OrderController(OrderService orderService, GoodsService goodsService) {
        this.orderService = orderService;
        this.goodsService = goodsService;
    }

    @GetMapping("detail")
    public @ResponseBody Result detail(SeckillUser user, Long orderId) {

        if (null == user) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        OrderInfo orderInfo = orderService.getById(orderId);
        if (null == orderInfo) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        Long goodsId = orderInfo.getGoodsId();
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);

        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setGoods(goodsVo);
        orderDetailVo.setOrder(orderInfo);

        return Result.success(orderDetailVo);
    }

}

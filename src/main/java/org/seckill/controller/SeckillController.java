package org.seckill.controller;

import cn.hutool.core.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.seckill.domain.SeckillUser;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.seckill.access.AccessLimit;
import org.seckill.common.redis.GoodsKey;
import org.seckill.domain.SeckillOrder;
import org.seckill.rabbitmq.MqSender;
import org.seckill.rabbitmq.dto.MiaoshaMessage;
import org.seckill.result.CodeMsg;
import org.seckill.result.Result;
import org.seckill.service.GoodsService;
import org.seckill.service.SeckillService;
import org.seckill.service.OrderService;
import org.seckill.service.RedisService;
import org.seckill.vo.GoodsVo;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zuquan Song
 *
 * @description MiaoshaController
 */
@Controller
@RequestMapping("miaosha")
@Slf4j
public class SeckillController implements InitializingBean {

    private final GoodsService goodsService;

    private final RedisService redisService;

    private final SeckillService seckillService;

    private final MqSender sender;

    private final OrderService orderService;

    private static final Map<Long, Boolean> LOCAL_GOODS_MAP = new ConcurrentHashMap <>();

    @Autowired
    public SeckillController(GoodsService goodsService,
                             RedisService redisService,
                             SeckillService seckillService,
                             MqSender sender,
                             OrderService orderService) {
        this.goodsService = goodsService;
        this.redisService = redisService;
        this.seckillService = seckillService;
        this.sender = sender;
        this.orderService = orderService;
    }

    @Override
    public void afterPropertiesSet() {
        List <GoodsVo> goodsVos = goodsService.listGoodsVo();
        if (null != goodsVos) {
            goodsVos.parallelStream().forEach(goodsVo -> {
                redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goodsVo.getId(), goodsVo.getStockCount());
                LOCAL_GOODS_MAP.put(goodsVo.getId(), false);
            });
        }
    }

    @AccessLimit(seconds = 5, maxCount = 5)
    @PostMapping("/{path}/do_miaosha")
    public @ResponseBody Result miaosha(SeckillUser user, Long goodsId, @PathVariable String path) {

        if (null == user) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        if (goodsId <= 0) {
            return Result.error(CodeMsg.GOODS_NOT_EXIST);
        }

        boolean checkMiaoshaPath = seckillService.checkMiaoshaPath(user, goodsId, path);
        if (!checkMiaoshaPath) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        if (LOCAL_GOODS_MAP.get(goodsId)) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (null != stock && stock < 0) {
            LOCAL_GOODS_MAP.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        SeckillOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        MiaoshaMessage miaoshaMessage = new MiaoshaMessage(user, goodsId);
        sender.miaoshaSender(miaoshaMessage);

        return Result.success(0);
    }

    @GetMapping("result")
    public @ResponseBody Result<Long> miaoshaResult(SeckillUser user, Long goodsId) {

        if (null == user) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        if (goodsId <= 0) {
            return Result.error(CodeMsg.GOODS_NOT_EXIST);
        }

        long orderId = seckillService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(orderId);
    }

    @AccessLimit(seconds = 5, maxCount = 5)
    @GetMapping("path")
    public @ResponseBody Result getMiaoshaPath(SeckillUser user, Long goodsId, Integer verifyCode) {

        if (null == user) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        if (goodsId <= 0) {
            return Result.error(CodeMsg.GOODS_NOT_EXIST);
        }

        boolean code = seckillService.checkMiaoshaVerifyCode(user, goodsId, verifyCode);
        if (!code) {
            return Result.error(CodeMsg.VERIFY_CODE_FAIL);
        }

        String path = seckillService.createMiaoshaPath(user, goodsId);

        return Result.success(path);
    }

    @AccessLimit(seconds = 5, maxCount = 50)
    @GetMapping("verifyCode")
    public @ResponseBody Result getMiaoshaVerifyCode(HttpServletResponse response, SeckillUser user, Long goodsId) {

        if (null == user) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        if (goodsId <= 0) {
            return Result.error(CodeMsg.GOODS_NOT_EXIST);
        }

        BufferedImage bufferedImage = seckillService.createMiaoshaVerifyCode(user, goodsId);

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            ImageIO.write(bufferedImage, ImageUtil.IMAGE_TYPE_JPEG, outputStream);
            outputStream.flush();
            return null;
        } catch (IOException e) {
            log.error(e.getMessage());
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }

}

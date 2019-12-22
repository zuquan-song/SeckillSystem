package org.seckill.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.script.ScriptUtil;
import lombok.extern.slf4j.Slf4j;
import org.seckill.common.redis.SeckillKey;
import org.seckill.domain.SeckillUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.seckill.domain.SeckillOrder;
import org.seckill.domain.OrderInfo;
import org.seckill.utils.MD5Util;
import org.seckill.vo.GoodsVo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author Zuquan Song
 *
 * @description MiaoshaService
 */
@Service
@Slf4j
public class SeckillService {

    private static final char[] OPS = new char[]{'+','-','*'};

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisService redisService;

    @Transactional(rollbackFor = Exception.class)
    public OrderInfo miaosha(SeckillUser user, GoodsVo goodsVo) {
        boolean reduceStock = goodsService.reduceStock(goodsVo);
        if (reduceStock) {
            return orderService.createOrder(user, goodsVo);
        } else {
            setGoodsOver(goodsVo.getId());
            return null;
        }
    }

    public long getMiaoshaResult(Long userId, Long goodsId) {

        SeckillOrder seckillOrder = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        if (null != seckillOrder) {
            return seckillOrder.getId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.IS_GOODS_OVER, ""+goodsId, true);
    }

    private boolean getGoodsOver(Long goodsId) {
        return redisService.exists(SeckillKey.IS_GOODS_OVER, ""+goodsId);
    }

    public String createMiaoshaPath(SeckillUser user, Long goodsId) {

        String path = MD5Util.md5(RandomUtil.randomString(32) + "&*!@:LJ:");

        redisService.set(SeckillKey.GET_MIAOSHA_PATH, ""+user.getId()+"_"+goodsId, path);
        return path;
    }

    public boolean checkMiaoshaPath(SeckillUser user, Long goodsId, String path) {
        if (null == user || null == path) {
            return false;
        }
        String pathOld = redisService.get(SeckillKey.GET_MIAOSHA_PATH, "" + user.getId() + "_" + goodsId, String.class);
        return Objects.equals(pathOld, path);
    }

    public BufferedImage createMiaoshaVerifyCode(SeckillUser user, Long goodsId) {

        int width = 80, height = 32;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();

        graphics.setColor(new Color(0xDCDCDC));
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(new Color(0, 0, 0));
        graphics.drawRect(0, 0, width -1, height -1);

        Random random = new Random();
        IntStream.range(0, 50).forEach(i->{
            int x = random.nextInt();
            int y = random.nextInt();
            graphics.drawOval(x, y, 0, 0);
        });

        String verifyCode = verifyCode(random);
        graphics.setColor(new Color(0, 100, 0));
        graphics.setFont(new Font("Candara", Font.BOLD, 24));
        graphics.drawString(verifyCode, 8, 24);
        graphics.dispose();

        int rnd = calc(verifyCode);
        redisService.set(SeckillKey.GET_MIAOSHA_VERIFY_CODE, ""+user.getId()+"_"+goodsId, rnd);

        return image;
    }

    private int calc(String exp) {
        return (Integer) ScriptUtil.eval(exp);
    }

    private String verifyCode(Random random) {
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);

        char op1 = OPS[random.nextInt(3)];
        char op2 = OPS[random.nextInt(3)];
        return String.format("%d%s%d%s%d", num1, op1, num2, op2, num3);
    }

    public boolean checkMiaoshaVerifyCode(SeckillUser user, Long goodsId, Integer verifyCode) {
        Integer oldCode = redisService.get(SeckillKey.GET_MIAOSHA_VERIFY_CODE, "" + user.getId() + "_" + goodsId, Integer.class);
        if (null == oldCode || oldCode - verifyCode != 0) {
            return false;
        }
        redisService.delete(SeckillKey.GET_MIAOSHA_VERIFY_CODE, "" + user.getId() + "_" + goodsId);
        return true;
    }
}

package org.seckill.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.seckill.domain.SeckillOrder;
import org.seckill.mapper.GoodsMapper;
import org.seckill.vo.GoodsVo;

import java.util.List;

/**
 * @author Zuquan Song
 *
 * @description GoodsService
 */
@Service
public class GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    public List<GoodsVo> listGoodsVo() {
        return goodsMapper.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsMapper.getGoodsVoByGoodsId(goodsId);
    }

    public boolean checkStockCount(Long goodsId) {
        GoodsVo goodsVo = this.getGoodsVoByGoodsId(goodsId);
        if (null != goodsVo) {
            Integer stockCount = goodsVo.getStockCount();
            return stockCount > 0;
        }
        return false;
    }

    public boolean reduceStock(GoodsVo goodsVo) {
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goodsVo.getId());
        int i = goodsMapper.reduceStock(seckillOrder);
        return i > 0;
    }
}

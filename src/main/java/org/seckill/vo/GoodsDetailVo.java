package org.seckill.vo;

import lombok.Data;
import org.seckill.domain.SeckillUser;

/**
 * @author Zuquan Song
 *
 * @description GoodsDetailVo
 */
@Data
public class GoodsDetailVo {
    private int miaoshaStatus = 0;
    private int remainSeconds = 0;
    private GoodsVo goods;
    private SeckillUser user;
}

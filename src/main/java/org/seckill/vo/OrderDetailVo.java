package org.seckill.vo;

import lombok.Data;
import org.seckill.domain.OrderInfo;

/**
 * @author Zuquan Song
 *
 * @description OrderDetailVo
 */
@Data
public class OrderDetailVo {
    private OrderInfo order;
    private GoodsVo goods;
}

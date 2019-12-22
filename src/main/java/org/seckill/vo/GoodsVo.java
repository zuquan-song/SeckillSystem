package org.seckill.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.seckill.domain.Goods;

import java.util.Date;

/**
 * @author Zuquan Song
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GoodsVo extends Goods {

    private Double miaoshaPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}

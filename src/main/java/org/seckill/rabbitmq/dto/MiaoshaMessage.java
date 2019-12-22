package org.seckill.rabbitmq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.seckill.domain.SeckillUser;

/**
 * @author Zuquan Song
 *
 * @description MiaoshaMessage
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiaoshaMessage {
    private SeckillUser user;
    private Long goodsId;
}

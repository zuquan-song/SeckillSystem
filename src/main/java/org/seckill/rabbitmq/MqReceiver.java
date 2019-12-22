package org.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.seckill.domain.SeckillUser;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.seckill.common.Constant;
import org.seckill.domain.SeckillOrder;
import org.seckill.rabbitmq.dto.MiaoshaMessage;
import org.seckill.service.GoodsService;
import org.seckill.service.SeckillService;
import org.seckill.service.OrderService;
import org.seckill.utils.ConvertUtil;
import org.seckill.vo.GoodsVo;

/**
 * @author Zuquan Song
 *
 * @description mq 接收端
 */
@Service
@Slf4j
public class MqReceiver {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillService seckillService;

    @RabbitListener(queues = Constant.MIAOSHA_QUEUE)
    public void miaoshaQueueReceiver(String message) {

        log.debug("miaoshaQueueReceiver msg={}", message);

        MiaoshaMessage miaoshaMessage = ConvertUtil.strToBean(message, MiaoshaMessage.class);
        SeckillUser user = miaoshaMessage.getUser();
        Long goodsId = miaoshaMessage.getGoodsId();

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        if (null == goodsVo || goodsVo.getStockCount() <= 0) {
//            log.error("miaoshaQueueReceiver, 库存不足, u={}, g={}", user.getId(), goodsId);
            return;
        }

        SeckillOrder seckillOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (null != seckillOrder) {
//            log.error("miaoshaQueueReceiver, 重复秒杀, u={}, g={}", user.getId(), goodsId);
            return;
        }

        seckillService.miaosha(user, goodsVo);
        log.info("miaoshaQueueReceiver. 秒杀成功. userId={}, goodsId={}", user.getId(), goodsVo.getId());
    }

    @RabbitListener(queues = Constant.DEFAULT_QUEUE_NAME)
    public void listenerQueue(String message) {
        log.info("receiver DEFAULT_QUEUE_NAME msg={}", message);
    }

    @RabbitListener(queues = Constant.TOPIC_QUEUE_1)
    public void listenerTopicQueue1(String message) {
        log.info("receiver TOPIC_QUEUE_1 msg={}", message);
    }
    @RabbitListener(queues = Constant.TOPIC_QUEUE_2)
    public void listenerTopicQueue2(String message) {
        log.info("receiver TOPIC_QUEUE_2 msg={}", message);
    }

    @RabbitListener(queues = Constant.FANOUT_QUEUE_1)
    public void listenerFanoutQueue1(String message) {
        log.info("receiver FANOUT_QUEUE_1 msg={}", message);
    }
    @RabbitListener(queues = Constant.FANOUT_QUEUE_2)
    public void listenerFanoutQueue2(String message) {
        log.info("receiver FANOUT_QUEUE_2 msg={}", message);
    }

    @RabbitListener(queues = Constant.HEADERS_QUEUE)
    public void listenerHeadersQueue(byte[] message) {
        log.info("receiver HEADERS_QUEUE msg={}", message);
    }
}

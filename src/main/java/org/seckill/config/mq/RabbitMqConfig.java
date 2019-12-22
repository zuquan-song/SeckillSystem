package org.seckill.config.mq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.seckill.common.Constant;

import java.util.HashMap;

/**
 * @author Zuquan Song
 *
 * @description RabbitMqConfig
 */
@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue miaoshaQueue() {
        return new Queue(Constant.MIAOSHA_QUEUE, true);
    }

    /**
     * Direct Mode,exchange
     */
    @Bean
    public Queue queue() {
        return new Queue(Constant.DEFAULT_QUEUE_NAME, true);
    }

    /**
     * Topic Mode, exchange
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(Constant.TOPIC_EXCHANGE);
    }

    @Bean
    public Queue topicQueue1() {
        return new Queue(Constant.TOPIC_QUEUE_1, true);
    }

    @Bean
    public Queue topicQueue2() {
        return new Queue(Constant.TOPIC_QUEUE_2, true);
    }

    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(Constant.ROUTING_KEY_1);
    }

    @Bean
    public Binding topicBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(Constant.ROUTING_KEY_2);
    }


    /**
     * Fanout Mode, exchange
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(Constant.FANOUT_EXCHANGE);
    }

    @Bean Queue fanoutQueue2() {
        return new Queue(Constant.FANOUT_QUEUE_2, true);
    }

    @Bean Queue fanoutQueue1() {
        return new Queue(Constant.FANOUT_QUEUE_1, true);
    }

    @Bean
    public Binding fanoutBinding1() {
        return BindingBuilder.bind(fanoutQueue1()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding2() {
        return BindingBuilder.bind(fanoutQueue2()).to(fanoutExchange());
    }


    /**
     * headers mode, exchange
     */
    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(Constant.HEADERS_EXCHANGE);
    }

    @Bean Queue headersQueue() {
        return new Queue(Constant.HEADERS_QUEUE, true);
    }

    @Bean
    public Binding headersBinding() {

        HashMap <String, Object> hashMap = new HashMap<String, Object>(2){{
            put("header1", "value1");
            put("header2", "value2");
        }};

        return BindingBuilder.bind(headersQueue()).to(headersExchange()).whereAll(hashMap).match();
    }

}

package org.seckill.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Zuquan Song
 *
 * @description RedisConfig
 */
@ConfigurationProperties("redis")
@Data
@Component
public class RedisConfig {
    private String host;
    private Integer port;
    /**
     * s
     */
    private Integer timeout;
    private String password;
    private Integer poolMaxTotal;
    private Integer poolMaxIdle;
    private Integer poolMaxWait;
    private Integer database;
}

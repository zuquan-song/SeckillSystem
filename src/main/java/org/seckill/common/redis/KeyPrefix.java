package org.seckill.common.redis;

/**
 * @author Zuquan Song
 *
 * @description KeyPrefix
 */
public interface KeyPrefix {

    /**
     * @return expire
     */
    int getExpireSeconds();

    /**
     * @return key prefix
     */
    String getPrefix();

    /**
     * @param key redis key
     * @return real key
     */
    String getRealKey(String key);
}

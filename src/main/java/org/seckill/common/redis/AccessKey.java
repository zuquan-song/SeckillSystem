package org.seckill.common.redis;

/**
 * @author Zuquan Song
 *
 * @description AccessKey
 */
public class AccessKey extends AbstractPrefix {
    private AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static AccessKey withExpireSeconds(int expireSeconds) {
        return new AccessKey(expireSeconds, "access");
    }
}

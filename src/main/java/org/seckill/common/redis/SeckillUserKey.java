package org.seckill.common.redis;

/**
 * @author Zuquan Song
 *
 * @description SeckillUserKey
 */
public class SeckillUserKey extends AbstractPrefix {

    private static final int TOKEN_EXPIRE = 3600 * 24 * 2;

    private SeckillUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static SeckillUserKey TOKEN = new SeckillUserKey(TOKEN_EXPIRE, "token");
    public static SeckillUserKey getById = new SeckillUserKey(0, "getById");

}

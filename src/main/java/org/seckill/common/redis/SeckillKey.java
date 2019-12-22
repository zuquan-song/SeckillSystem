package org.seckill.common.redis;

/**
 * @author Zuquan Song
 *
 * @description SeckillKey
 */
public class SeckillKey extends AbstractPrefix {
    private SeckillKey(String prefix) {
        super(prefix);
    }

    private SeckillKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static final SeckillKey IS_GOODS_OVER = new SeckillKey("go");
    public static final SeckillKey GET_MIAOSHA_PATH = new SeckillKey(60, "gp");
    public static final SeckillKey GET_MIAOSHA_VERIFY_CODE = new SeckillKey(300, "vc");
}

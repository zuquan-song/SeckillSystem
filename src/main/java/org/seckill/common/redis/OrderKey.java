package org.seckill.common.redis;

/**
 * @author Zuquan Song
 *
 * @description OrderKey
 */
public class OrderKey extends AbstractPrefix {


    private OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("moug");
}

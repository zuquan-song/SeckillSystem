package org.seckill.common.redis;

/**
 * @author Zuquan Song
 *
 * @description AbstractPrefix
 */
public abstract class AbstractPrefix implements KeyPrefix {

    private int expireSeconds;

    private String prefix;

    AbstractPrefix(String prefix) {
        this(0, prefix);
    }

    AbstractPrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    /**
     *
     * @return expire
     */
    @Override
    public int getExpireSeconds() {
        return this.expireSeconds;
    }

    /**
     *
     * @return key prefix
     */
    @Override
    public String getPrefix() {
        String simpleName = getClass().getSimpleName();
        return simpleName + ":" + this.prefix;
    }

    @Override
    public String getRealKey(String key) {
        return getPrefix() + key;
    }
}

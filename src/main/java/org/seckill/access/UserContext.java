package org.seckill.access;

import org.seckill.domain.SeckillUser;

/**
 * @author Zuquan Song
 *
 * @description UserContext
 */
public class UserContext {

    private static final ThreadLocal<SeckillUser> USER_HOLDER = new ThreadLocal <>();

    public static SeckillUser getUser() {
        return USER_HOLDER.get();
    }

    public static void setUser(SeckillUser user) {
        USER_HOLDER.set(user);
    }

    public static void remove() {
        USER_HOLDER.remove();
    }
}

package org.seckill.common.redis;

/**
 * @author Zuquan Song
 *
 * @description UserKey
 */
public class UserKey extends AbstractPrefix {

    private UserKey(String prefix) {
        super(prefix);
    }

    public static UserKey GET_BY_ID = new UserKey("id");
    public static UserKey GET_BY_NAME = new UserKey("name");

}

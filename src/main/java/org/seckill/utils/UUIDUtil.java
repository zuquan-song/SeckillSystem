package org.seckill.utils;

import java.util.UUID;

/**
 * @author Zuquan Song
 *
 * @description UUIDUtil
 */
public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}

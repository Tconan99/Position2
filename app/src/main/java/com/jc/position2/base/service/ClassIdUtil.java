package com.jc.position2.base.service;

/**
 * Created by tconan on 16/4/19.
 */
public class ClassIdUtil {
    public static long ids = 100;
    public static synchronized long getId() {
        if (ids > Long.MAX_VALUE) {
            ids = 100;
        }
        return ids++;
    }
}

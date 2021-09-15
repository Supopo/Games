package com.xxx.games.utils;

import java.util.Random;

/**
 * Created by Supopo. on 2021/9/15.
 */
public class BaseUtils {
    public static int getRandom(int num) {
        Random random = new Random();
        int s = random.nextInt(num);
        return s;
    }
}

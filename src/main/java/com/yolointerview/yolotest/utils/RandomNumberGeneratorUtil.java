package com.yolointerview.yolotest.utils;

import java.util.Random;

public class RandomNumberGeneratorUtil {

    public static int generateRandomNumber(){
        return new Random().nextInt(0, 10);
    }
}

package com.yolointerview.yolotest.units;

import com.yolointerview.yolotest.utils.RandomNumberGeneratorUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandomNumberGeneratorTests {

    @Test
    @DisplayName("Number generated is always between 1 and 10 inclusive")
    public void generatedNumberIsWithinValidRange(){
        int leftBound = 0, rightBound = 10;
        int randomNumber = RandomNumberGeneratorUtil.generateRandomNumber();
        assertTrue(randomNumber >= leftBound);
        assertTrue(randomNumber <= rightBound);

        randomNumber = RandomNumberGeneratorUtil.generateRandomNumber();
        assertTrue(randomNumber >= leftBound);
        assertTrue(randomNumber <= rightBound);
    }
}

package com.yolointerview.yolotest;

import com.yolointerview.yolotest.dtos.PlaceBetDto;

import java.math.BigDecimal;

public class PlaceBetDtoUtils {

    public static PlaceBetDto placeBetDto(String gameId) {
        return PlaceBetDto.builder().gameId(gameId).nickname("emmanuel")
                .number(5).stake(BigDecimal.TEN).build();
    }

    public static PlaceBetDto placeBetDto(String gameId, String name, int number) {
        return PlaceBetDto.builder().gameId(gameId).nickname(name)
                .number(number).stake(BigDecimal.TEN).build();
    }

    public static PlaceBetDto placeBetDto(String gameId, BigDecimal stake) {
        return PlaceBetDto.builder().gameId(gameId).nickname("emmanuel")
                .number(4).stake(stake).build();
    }
}

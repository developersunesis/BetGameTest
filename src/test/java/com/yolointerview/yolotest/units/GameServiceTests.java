package com.yolointerview.yolotest.units;

import com.yolointerview.yolotest.PlaceBetDto;
import com.yolointerview.yolotest.exceptions.GameDoesNotExistException;
import com.yolointerview.yolotest.service.GameServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameServiceTests {

    private static GameServiceImpl gameService;

    @BeforeEach
    public void initTests(){
        gameService = new GameServiceImpl();
    }

    @Test
    @DisplayName("Throws error when the game session requested doesn't exist")
    public void requestedGameIdSessionDoesNotExist(){
        String invalidGameId = UUID.randomUUID().toString();
        PlaceBetDto placeBetDto = PlaceBetDto.builder().gameId(invalidGameId)
                .nickname("emmanuel").number(5).stake(BigDecimal.TEN).build();
        assertThrows(GameDoesNotExistException.class, () -> gameService.placeBet(placeBetDto));
    }
}

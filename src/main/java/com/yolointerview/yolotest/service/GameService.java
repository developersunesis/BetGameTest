package com.yolointerview.yolotest.service;

import com.yolointerview.yolotest.PlaceBetDto;
import com.yolointerview.yolotest.entities.Game;

public interface GameService {
    void placeBet(PlaceBetDto placeBetDto);
    boolean isGameAvailable(String id);
    Game startNewGame(Game game);
}

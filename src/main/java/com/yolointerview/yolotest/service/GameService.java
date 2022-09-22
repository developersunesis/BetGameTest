package com.yolointerview.yolotest.service;

import com.yolointerview.yolotest.PlaceBetDto;
import com.yolointerview.yolotest.entities.Game;
import com.yolointerview.yolotest.exceptions.DuplicateGameIdException;
import com.yolointerview.yolotest.exceptions.GameDoesNotExistException;

public interface GameService {
    void placeBet(PlaceBetDto placeBetDto);

    boolean isGameAvailable(String id);

    Game startNewGame(Game game) throws DuplicateGameIdException;

    Game getGameById(String id) throws GameDoesNotExistException;

    Game endGame(String id);
}

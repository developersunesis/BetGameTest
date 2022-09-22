package com.yolointerview.yolotest.service;

import com.yolointerview.yolotest.dtos.PlaceBetDto;
import com.yolointerview.yolotest.entities.Game;
import com.yolointerview.yolotest.exceptions.DuplicateGameIdException;
import com.yolointerview.yolotest.exceptions.GameDoesNotExistException;
import com.yolointerview.yolotest.exceptions.GameTimedOutException;

import java.util.concurrent.ConcurrentHashMap;

public interface GameService {
    void placeBet(PlaceBetDto placeBetDto);

    boolean isGameAvailable(String id);

    Game startNewGame(Game game) throws DuplicateGameIdException;

    Game getGameById(String id) throws GameDoesNotExistException;

    Game endGame(String id) throws GameDoesNotExistException, GameTimedOutException;

    int generateRandomNumber();

    ConcurrentHashMap<String, Game> getGames();
}

package com.gameapp.test.service;

import com.gameapp.test.dtos.PlaceBetDto;
import com.gameapp.test.entities.Game;
import com.gameapp.test.exceptions.DuplicateGameIdException;
import com.gameapp.test.exceptions.GameDoesNotExistException;
import com.gameapp.test.exceptions.GameTimedOutException;

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

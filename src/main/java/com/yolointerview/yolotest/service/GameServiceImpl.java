package com.yolointerview.yolotest.service;

import com.yolointerview.yolotest.PlaceBetDto;
import com.yolointerview.yolotest.entities.Game;
import com.yolointerview.yolotest.exceptions.GameDoesNotExistException;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameServiceImpl implements GameService {

    /**
     * The use of ConcurrentHashMap helps ensure that updates and access to the list of games
     * is not blocked by any process. This way we avoid getting a ConcurrentModificationException
     * in a situation whe access and update are being performed simultaneously
     */
    private final ConcurrentHashMap<String, Game> games = new ConcurrentHashMap<>();

    @Override
    @SneakyThrows
    public void placeBet(PlaceBetDto placeBetDto) {
        if(isGameAvailable(placeBetDto.getGameId())){

        }

        throw new GameDoesNotExistException();
    }

    @Override
    public boolean isGameAvailable(String id) {
        return false;
    }

    @Override
    public Game startNewGame(Game game) {
        return null;
    }


}

package com.yolointerview.yolotest.service;

import com.yolointerview.yolotest.PlaceBetDto;
import com.yolointerview.yolotest.entities.Game;
import com.yolointerview.yolotest.entities.Player;
import com.yolointerview.yolotest.exceptions.DuplicateGameIdException;
import com.yolointerview.yolotest.exceptions.GameDoesNotExistException;
import com.yolointerview.yolotest.exceptions.GameTimedOutException;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;
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
        String gameId = placeBetDto.getGameId();
        Game game = getGameById(gameId);

        if (isGameTimedOut(game)) throw new GameTimedOutException();

        // add a new player to the game session
        Player newPlayer = new Player(placeBetDto);
        HashMap<String, Player> playerHashMap = game.getPlayers();
        playerHashMap.put(newPlayer.getId(), newPlayer);
    }

    @Override
    public boolean isGameAvailable(String id) {
        if (id == null) throw new NullPointerException();
        return Objects.nonNull(games.get(id));
    }

    @Override
    public Game startNewGame(Game game) throws DuplicateGameIdException {
        String gameId = game.getId();

        // rare, but if the id of the new game to be played exists, we throw an exception
        if (isGameAvailable(gameId)) throw new DuplicateGameIdException();

        // add new game to existing collections of game
        games.put(gameId, game);
        return game;
    }

    @Override
    public Game getGameById(String id) throws GameDoesNotExistException {
        if (!isGameAvailable(id)) throw new GameDoesNotExistException();
        return games.get(id);
    }

    @Override
    public Game endGame(String id) {
        return null;
    }

    /**
     * Validate that a game is not yet timed out by comparing with the current date time
     * from LocalDateTime.now()
     *
     * @param game A Game instance
     * @return Returns false if the game is timed out
     */
    private boolean isGameTimedOut(Game game) {
        LocalDateTime timeout = game.getTimeout();
        return timeout.isBefore(LocalDateTime.now());
    }

}

package com.gameapp.test.service;

import com.gameapp.test.dtos.PlaceBetDto;
import com.gameapp.test.entities.Game;
import com.gameapp.test.entities.Player;
import com.gameapp.test.enums.StakeStatus;
import com.gameapp.test.exceptions.DuplicateGameIdException;
import com.gameapp.test.exceptions.GameDoesNotExistException;
import com.gameapp.test.exceptions.GameTimedOutException;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
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

        if (!game.isActive()) throw new GameTimedOutException();

        // add a new player to the game session
        Player newPlayer = Player.newInstance(placeBetDto);
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
        game.setActive(true);
        games.put(gameId, game);
        return game;
    }

    @Override
    public Game getGameById(String id) throws GameDoesNotExistException {
        if (!isGameAvailable(id)) throw new GameDoesNotExistException();
        return games.get(id);
    }

    @Override
    public Game endGame(String id) throws GameDoesNotExistException, GameTimedOutException {
        Game game = getGameById(id);

        // if game is not active throw exception
        if (!game.isActive()) throw new GameTimedOutException();

        // generate a random number as correct number for the game
        int serverGeneratedRandomNumber = generateRandomNumber();
        game.setCorrectNumber(serverGeneratedRandomNumber);
        game.setEndedAt(new Date());
        game.setActive(false);

        // reward every user accordingly after ended game
        BigDecimal gameWinningFactor = BigDecimal.valueOf(game.getWinningFactor());
        HashMap<String, Player> playerHashMap = game.getPlayers();
        playerHashMap.forEach((s, player) -> {
            int correctNumber = game.getCorrectNumber();
            int guessedNumber = player.getGuessedNumber();
            if (guessedNumber == correctNumber) {
                BigDecimal playerStake = player.getStakeAmount();
                BigDecimal balance = gameWinningFactor.multiply(playerStake);
                balance = balance.setScale(2, RoundingMode.UP);
                player.setStakeStatus(StakeStatus.WIN);
                player.setEndOfGameBalance(balance);
            } else {
                player.setStakeStatus(StakeStatus.LOSS);
            }
        });

        return game;
    }

    @Override
    public int generateRandomNumber() {
        return new Random().nextInt(1, 10);
    }

    public ConcurrentHashMap<String, Game> getGames() {
        return games;
    }
}

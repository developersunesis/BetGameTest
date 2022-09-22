package com.yolointerview.yolotest.handlers;

import com.yolointerview.yolotest.dtos.MessageDto;
import com.yolointerview.yolotest.dtos.MessageDtoConverter;
import com.yolointerview.yolotest.dtos.PlaceBetDto;
import com.yolointerview.yolotest.entities.Game;
import com.yolointerview.yolotest.enums.MessageType;
import com.yolointerview.yolotest.service.GameService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.yolointerview.yolotest.enums.MessageType.*;

@Slf4j
@Component
public class GameSocketHandler extends TextWebSocketHandler {

    private final GameService gameService;
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private Game currentGame;

    public GameSocketHandler(GameService gameService) {
        this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2);
        this.gameService = gameService;
        startNewGame();
    }

    @SneakyThrows
    private void startNewGame() {
        String id = UUID.randomUUID().toString();
        currentGame = new Game(id);
        currentGame = gameService.startNewGame(currentGame);

        // end the current game and start a new one after timeout
        long timeout = currentGame.getTimeout();
        scheduledThreadPoolExecutor.schedule(this::endCurrentGame, timeout, TimeUnit.SECONDS);
        scheduledThreadPoolExecutor.schedule(this::startNewGame, timeout, TimeUnit.SECONDS);

        log.info("New Game Started {{}}", id);
    }

    @SneakyThrows
    private void endCurrentGame() {
        String id = currentGame.getId();

        log.info("Ended Current Game {{}}", id);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        MessageType messageType = MessageDtoConverter.getMessageType(payload);
        switch (messageType) {
            case PING -> sendResponseToPing(session);
            case PLACE_BET -> sendResponseAfterPlacingBet(session, payload);
            default -> sendErrorResponse(session, "Unable to parse request type");
        }
    }

    private void sendResponseToPing(WebSocketSession session) throws IOException {
        MessageDto<ConcurrentHashMap<String, Game>> messageDto = new MessageDto<>(PING);
        ConcurrentHashMap<String, Game> gameConcurrentHashMap = gameService.getGames();
        messageDto.setData(gameConcurrentHashMap);
        session.sendMessage(messageDto.asTextMessage());
    }

    private void sendResponseAfterPlacingBet(WebSocketSession session, String payload) throws IOException {
        log.info("Session{{}} is placing a Bet{} on Game{{}}", session.getId(), payload, currentGame.getId());
        MessageDto<PlaceBetDto> placeBetDtoMessageDto = MessageDtoConverter
                .convertToMessageDto(payload, PlaceBetDto.class);
        PlaceBetDto placeBetDto = placeBetDtoMessageDto.getData();
        String nickname = placeBetDto.getNickname();
        Integer guessedNumber = placeBetDto.getNumber();
        BigDecimal stake = placeBetDto.getStake();

        // validate the bet request
        if (!StringUtils.hasText(nickname)) {
            sendErrorResponse(session, "Please provide a nickname");
            return;
        } else if (guessedNumber == null || guessedNumber < 1 || guessedNumber > 10) {
            sendErrorResponse(session, "Please provide a valid number between 1 and 10");
            return;
        } else if (stake == null) {
            sendErrorResponse(session, "Please specify your bet amount");
            return;
        } else if (stake.doubleValue() <= 0) {
            sendErrorResponse(session, "Please specify a bet amount greater than 0");
            return;
        }

        // place a bet on the current game
        gameService.placeBet(placeBetDto);

        // send back response to client
        MessageDto<Game> responseMessageDto = new MessageDto<>(PLACE_BET);
        responseMessageDto.setData(currentGame);
        session.sendMessage(responseMessageDto.asTextMessage());
    }

    private void sendErrorResponse(WebSocketSession session, String message) throws IOException {
        log.error("Session{{}} experienced an error ({})", session.getId(), message);
        MessageDto<?> responseMessageDto = new MessageDto<>(ERROR);
        responseMessageDto.setMessage(message);
        session.sendMessage(responseMessageDto.asTextMessage());
    }

    public Game getCurrentGame() {
        return currentGame;
    }
}

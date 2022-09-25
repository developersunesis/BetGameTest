package com.yolointerview.yolotest;

import com.yolointerview.yolotest.dtos.MessageDto;
import com.yolointerview.yolotest.dtos.MessageDtoConverter;
import com.yolointerview.yolotest.dtos.PlaceBetDto;
import com.yolointerview.yolotest.entities.Game;
import com.yolointerview.yolotest.entities.Player;
import com.yolointerview.yolotest.enums.StakeStatus;
import com.yolointerview.yolotest.handlers.GameSocketHandler;
import com.yolointerview.yolotest.service.GameService;
import com.yolointerview.yolotest.service.GameServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.yolointerview.yolotest.PlaceBetDtoUtils.placeBetDto;
import static com.yolointerview.yolotest.enums.MessageType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Integration Tests
class YoloTestApplicationTests {

    private static final GameService gameService = spy(new GameServiceImpl());
    private static final int MOCK_CLIENT_SIZE = 5;
    private static final TestClientWebSocket[] testClientWebSockets = new TestClientWebSocket[MOCK_CLIENT_SIZE];
    private GameSocketHandler gameSocketHandler;

    @BeforeAll
    public static void initDeps() {
        // create MOCK_CLIENT_SIZE amount of mock sockets
        for (int i = 0; i < MOCK_CLIENT_SIZE; i++) {
            testClientWebSockets[i] = new TestClientWebSocket(mock(WebSocketSession.class));
        }
    }

    @BeforeEach
    public void instantiateGameSocketHandler() {
        gameSocketHandler = new GameSocketHandler(gameService);
    }

    @Test
    @DisplayName("Given client sends a ping, expect a response ping from the server with active games")
    void pingResponseReceivedFromServeWithActiveGame() throws Exception {
        TestClientWebSocket testClientWebSocket = testClientWebSockets[0];
        MessageDto<?> messageDto = new MessageDto<>(PING);
        gameSocketHandler.handleMessage(testClientWebSocket, messageDto.asTextMessage());

        ArrayList<TextMessage> textMessages = testClientWebSocket.getMessages();
        assertFalse(textMessages.isEmpty());

        // a ping response is received
        String payload = textMessages.get(0).getPayload();
        MessageDto<ConcurrentHashMap<String, Game>> responseMessage = MessageDtoConverter.convertToMessageDto(payload,
                ConcurrentHashMap.class);
        assertEquals(PING, responseMessage.getType());

        // response contains list of all games
        ConcurrentHashMap<String, Game> data = responseMessage.getData();
        assertNotNull(data);
        assertFalse(data.isEmpty());
    }

    @Test
    @DisplayName("Given there's an active game and player places bet but fails validations")
    void playerUnableToPlacesBetForActiveGame() throws Exception {
        TestClientWebSocket testClientWebSocket = testClientWebSockets[0];
        TestClientWebSocket testClientWebSocket1 = testClientWebSockets[1];
        TestClientWebSocket testClientWebSocket2 = testClientWebSockets[2];
        TestClientWebSocket testClientWebSocket3 = testClientWebSockets[3];

        // clear all client messages
        testClientWebSocket.clearMessages();
        testClientWebSocket1.clearMessages();
        testClientWebSocket2.clearMessages();
        testClientWebSocket3.clearMessages();

        Game currentGame = gameSocketHandler.getCurrentGame();
        String gameId = currentGame.getId();

        // client A did not provide a name
        MessageDto<PlaceBetDto> messageDto = new MessageDto<>(PLACE_BET);
        PlaceBetDto placeBetDto = placeBetDto(gameId, null, 5);
        messageDto.setData(placeBetDto);

        // client B provided an invalid number
        MessageDto<PlaceBetDto> messageDto1 = new MessageDto<>(PLACE_BET);
        PlaceBetDto placeBetDto1 = placeBetDto(gameId, "emmanuel", -1);
        messageDto1.setData(placeBetDto1);

        // client C did not provide a valid stake amount
        MessageDto<PlaceBetDto> messageDto2 = new MessageDto<>(PLACE_BET);
        PlaceBetDto placeBetDto2 = placeBetDto(gameId, null);
        messageDto2.setData(placeBetDto2);

        // client D provided an invalid game id
        MessageDto<PlaceBetDto> messageDto3 = new MessageDto<>(PLACE_BET);
        PlaceBetDto placeBetDto3 = placeBetDto(UUID.randomUUID().toString(), BigDecimal.TEN);
        messageDto3.setData(placeBetDto3);

        // all clients placed a bet
        gameSocketHandler.handleMessage(testClientWebSocket, messageDto.asTextMessage());
        gameSocketHandler.handleMessage(testClientWebSocket1, messageDto1.asTextMessage());
        gameSocketHandler.handleMessage(testClientWebSocket2, messageDto2.asTextMessage());
        gameSocketHandler.handleMessage(testClientWebSocket3, messageDto3.asTextMessage());

        String payloadClientA = testClientWebSocket.getMessages().get(0).getPayload();
        String payloadClientB = testClientWebSocket1.getMessages().get(0).getPayload();
        String payloadClientC = testClientWebSocket2.getMessages().get(0).getPayload();
        String payloadClientD = testClientWebSocket3.getMessages().get(0).getPayload();

        MessageDto<?> responseClientA = MessageDtoConverter.convertToMessageDto(payloadClientA);
        MessageDto<?> responseClientB = MessageDtoConverter.convertToMessageDto(payloadClientB);
        MessageDto<?> responseClientC = MessageDtoConverter.convertToMessageDto(payloadClientC);
        MessageDto<?> responseClientD = MessageDtoConverter.convertToMessageDto(payloadClientD);

        assertEquals(ERROR, responseClientA.getType());
        assertEquals(ERROR, responseClientB.getType());
        assertEquals(ERROR, responseClientC.getType());
        assertEquals(ERROR, responseClientD.getType());

        assertEquals("Please provide a nickname", responseClientA.getMessage());
        assertEquals("Please provide a valid number between 1 and 10", responseClientB.getMessage());
        assertEquals("Please specify your bet amount", responseClientC.getMessage());
        assertEquals("Requested game does not exist", responseClientD.getMessage());

        // no player ends up in the game
        assertTrue(currentGame.getPlayers().isEmpty());
    }

    @Test
    @DisplayName("Given there's an active game, player places bet successfully")
    void playerSuccessfullyPlacesBetForActiveGame() throws Exception {
        TestClientWebSocket testClientWebSocket = testClientWebSockets[0];
        testClientWebSocket.clearMessages();

        Game currentGame = gameSocketHandler.getCurrentGame();
        String currentGameId = currentGame.getId();
        MessageDto<PlaceBetDto> messageDto = new MessageDto<>(PLACE_BET);
        PlaceBetDto placeBetDto = placeBetDto(currentGameId);
        messageDto.setData(placeBetDto);
        gameSocketHandler.handleMessage(testClientWebSocket, messageDto.asTextMessage());

        String payload = testClientWebSocket.getMessages().get(0).getPayload();
        MessageDto<Game> gameMessageDto = MessageDtoConverter.convertToMessageDto(payload, Game.class);
        Game game = gameMessageDto.getData();
        assertEquals(PLACE_BET, gameMessageDto.getType());
        assertEquals(currentGameId, game.getId());
        assertFalse(game.getPlayers().isEmpty());

        Optional<Player> optionalPlayer = game.getPlayers().values().stream().findFirst();
        assertTrue(optionalPlayer.isPresent());
        Player player = optionalPlayer.get();
        assertNull(player.getStakeStatus());
        assertEquals(placeBetDto.getNickname(), player.getNickname());
        assertEquals(BigDecimal.ZERO, player.getEndOfGameBalance());
    }

    @Test
    @DisplayName("Given there's an active game and 5 players places bet successfully, " +
            "send game feedback to all players when game ends")
    void fivePlayerSuccessfullyPlacesBetForActiveGameAndGotFeedback() throws Exception {
        // clear messages for all clients
        for (TestClientWebSocket testClientWebSocket : testClientWebSockets) {
            testClientWebSocket.clearMessages();
        }

        Game currentGame = gameSocketHandler.getCurrentGame();
        String currentGameId = currentGame.getId();

        String[] players = {"bob", "joe", "mean", "alice", "sandra"};
        int[] playersNumbers = {3, 4, 2, 1, 8};

        // establish all players connection and submit all players bets
        TestClientWebSocket testClientWebSocket = null;
        MessageDto<PlaceBetDto> messageDto = new MessageDto<>(PLACE_BET);
        for (int i = 0; i < testClientWebSockets.length; i++) {
            testClientWebSocket = testClientWebSockets[i];
            PlaceBetDto placeBetDto = placeBetDto(currentGameId, players[i], playersNumbers[i]);
            messageDto.setData(placeBetDto);
            gameSocketHandler.afterConnectionEstablished(testClientWebSocket);
            gameSocketHandler.handleMessage(testClientWebSocket, messageDto.asTextMessage());
        }

        // check that the websocket it not null
        assertNotNull(testClientWebSocket);

        // get last payload response to the last client for validation
        String payload = testClientWebSocket.getMessages().get(0).getPayload();
        MessageDto<Game> gameMessageDto = MessageDtoConverter.convertToMessageDto(payload, Game.class);
        Game game = gameMessageDto.getData();
        assertEquals(PLACE_BET, gameMessageDto.getType());
        assertEquals(currentGameId, game.getId());
        assertFalse(game.getPlayers().isEmpty());
        assertEquals(5, game.getPlayers().size());

        // mock the game and assign a known number for winning
        // at the end of the game, alice is meant to be the standing winner
        when(gameService.generateRandomNumber()).thenReturn(1);

        // run a schedule a bit after the game timed out
        long scheduleCheckTimeout = game.getTimeout() + 1000;

        // use a schedule thread pool to wait for the game to end
        // and check the message sent to all client websocket
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.schedule(() -> {
            Game lastGame = null;
            for (TestClientWebSocket clientWebSocket : testClientWebSockets) {
                ArrayList<TextMessage> messages = clientWebSocket.getMessages();
                String lastPayload = messages.get(messages.size() - 2).getPayload();
                MessageDto<Game> lastMessageDto = MessageDtoConverter.convertToMessageDto(lastPayload, Game.class);
                lastGame = lastMessageDto.getData();
                assertEquals(TIMED_OUT, lastMessageDto.getType());
                assertFalse(lastGame.isActive());
            }

            // the mocked correct number of current game
            assertEquals(1, lastGame.getCorrectNumber());

            List<Player> playerList = lastGame.getPlayers().values().stream().toList();
            // 4 losing players
            assertEquals(4, playerList.stream().filter(player ->
                    player.getStakeStatus() == StakeStatus.LOSS).count());

            // 1 winning player: alice
            List<Player> winningPlayers = playerList.stream().filter(player ->
                    player.getStakeStatus() == StakeStatus.WIN).toList();
            assertEquals(1, winningPlayers.size());

            // player details matches: alice
            Player player = winningPlayers.get(0);
            BigDecimal expectedBalance = BigDecimal.valueOf(99).setScale(2, RoundingMode.UP);
            assertEquals("alice", player.getNickname());
            assertEquals(expectedBalance, player.getEndOfGameBalance());
        }, scheduleCheckTimeout, TimeUnit.MILLISECONDS).get();
    }
}

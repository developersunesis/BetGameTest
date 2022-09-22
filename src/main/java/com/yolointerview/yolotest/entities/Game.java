package com.yolointerview.yolotest.entities;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;

@Getter
public class Game {
    private final String id;
    private Integer correctNumber;
    private final HashMap<String, Player> players;
    private final LocalDateTime timeout;

    public Game(String id) {
        this.id = id;
        this.players = new HashMap<>();
        this.timeout = LocalDateTime.now().plusSeconds(10);
    }

    public void setCorrectNumber(Integer correctNumber) {
        this.correctNumber = correctNumber;
    }
}

package com.yolointerview.yolotest.entities;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;

@Getter
public class Game {
    private final String id;
    private Integer correctNumber;
    private final HashMap<String, Player> players;
    private final double winningFactor = 9.9;
    private final LocalDateTime timeout;
    private boolean active;

    public Game(String id) {
        this.id = id;
        this.players = new HashMap<>();
        this.timeout = LocalDateTime.now().plusSeconds(10);
    }

    public void setCorrectNumber(Integer correctNumber) {
        this.correctNumber = correctNumber;
    }

    public double getWinningFactor() {
        return winningFactor;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

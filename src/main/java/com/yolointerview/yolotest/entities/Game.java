package com.yolointerview.yolotest.entities;

import lombok.Getter;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Getter
public class Game {
    private final String id;
    private Integer correctNumber;
    private final HashMap<String, Player> players;
    private final double winningFactor = 9.9;
    private final long timeout = 10_000;
    private final Date createdAt;
    private Date endedAt;
    private boolean active;

    public Game() {
        this.id = UUID.randomUUID().toString();
        this.players = new HashMap<>();
        this.createdAt = new Date();
    }

    public Game(String id) {
        this.id = id;
        this.players = new HashMap<>();
        this.createdAt = new Date();
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

    public void setEndedAt(Date endedAt) {
        this.endedAt = endedAt;
    }
}

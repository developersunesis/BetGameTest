package com.yolointerview.yolotest.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

@Getter
@Setter
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

    private Date setTimeout(){
        throw new UnsupportedOperationException();
    }
}

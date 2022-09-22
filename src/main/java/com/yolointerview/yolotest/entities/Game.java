package com.yolointerview.yolotest.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Game {
    private String id;

    private Integer correctNumber;

    private Set<Player> players = new HashSet<>();
}

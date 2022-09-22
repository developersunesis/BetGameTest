package com.yolointerview.yolotest.entities;

import com.yolointerview.yolotest.dtos.PlaceBetDto;
import com.yolointerview.yolotest.enums.StakeStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class Player {
    private final String id;
    private String nickname;
    private int guessedNumber;
    private StakeStatus stakeStatus;
    private BigDecimal stakeAmount;
    private BigDecimal endOfGameBalance = BigDecimal.ZERO;

    public Player() {
        this.id = UUID.randomUUID().toString();
        this.nickname = null;
        this.guessedNumber = 0;
        this.stakeAmount = null;
    }

    public static Player newInstance(PlaceBetDto placeBetDto) {
        Player player = new Player();
        player.nickname = placeBetDto.getNickname();
        player.stakeAmount = placeBetDto.getStake();
        player.guessedNumber = placeBetDto.getNumber();
        return player;
    }

    public void setStakeStatus(StakeStatus stakeStatus) {
        this.stakeStatus = stakeStatus;
    }

    public void setEndOfGameBalance(BigDecimal endOfGameBalance) {
        this.endOfGameBalance = endOfGameBalance;
    }
}

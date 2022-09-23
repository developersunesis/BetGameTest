package com.yolointerview.yolotest.entities;

import com.yolointerview.yolotest.dtos.PlaceBetDto;
import com.yolointerview.yolotest.enums.StakeStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
public class Player {
    private final String id;
    private String nickname;
    private int guessedNumber;
    private StakeStatus stakeStatus;
    private BigDecimal stakeAmount;
    private BigDecimal endOfGameBalance = BigDecimal.ZERO;
    private final Date createdAt;
    private String sessionId;

    public Player() {
        this.id = UUID.randomUUID().toString();
        this.nickname = null;
        this.guessedNumber = 0;
        this.stakeAmount = null;
        this.createdAt = new Date();
    }

    public static Player newInstance(PlaceBetDto placeBetDto) {
        Player player = new Player();
        player.nickname = placeBetDto.getNickname();
        player.stakeAmount = placeBetDto.getStake();
        player.guessedNumber = placeBetDto.getNumber();
        player.sessionId = placeBetDto.getSessionId();
        return player;
    }

    public void setStakeStatus(StakeStatus stakeStatus) {
        this.stakeStatus = stakeStatus;
    }

    public void setEndOfGameBalance(BigDecimal endOfGameBalance) {
        this.endOfGameBalance = endOfGameBalance;
    }
}

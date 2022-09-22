package com.yolointerview.yolotest.entities;

import com.yolointerview.yolotest.dtos.PlaceBetDto;
import com.yolointerview.yolotest.enums.StakeStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class Player {
    private final String id;
    private final String nickname;
    private final int guessedNumber;
    private StakeStatus stakeStatus;
    private final BigDecimal stakeAmount;
    private BigDecimal endOfGameBalance = BigDecimal.ZERO;

    public Player(PlaceBetDto placeBetDto) {
        this.id = UUID.randomUUID().toString();
        this.nickname = placeBetDto.getNickname();
        this.stakeAmount = placeBetDto.getStake();
        this.guessedNumber = placeBetDto.getNumber();
    }

    public void setStakeStatus(StakeStatus stakeStatus) {
        this.stakeStatus = stakeStatus;
    }

    public void setEndOfGameBalance(BigDecimal endOfGameBalance) {
        this.endOfGameBalance = endOfGameBalance;
    }
}

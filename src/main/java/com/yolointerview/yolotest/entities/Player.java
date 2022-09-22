package com.yolointerview.yolotest.entities;

import com.yolointerview.yolotest.enums.StakeStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Player {
    private String id;
    private String nickname;
    private StakeStatus stakeStatus;
    private BigDecimal stakeAmount = BigDecimal.ZERO;
    private BigDecimal endOfGameBalance = BigDecimal.ZERO;
}

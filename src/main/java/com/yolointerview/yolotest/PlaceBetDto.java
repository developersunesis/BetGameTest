package com.yolointerview.yolotest;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PlaceBetDto {
    private String gameId;
    private String nickname;
    private Integer number;
    private BigDecimal stake;
}

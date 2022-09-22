package com.yolointerview.yolotest;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PlaceBetDto {
    private String gameId;
    private String nickname;
    private Integer number;
    private BigDecimal stake;
}

package com.yolointerview.yolotest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceBetDto {
    private String gameId;
    private String nickname;
    private Integer number;
    private BigDecimal stake;
    private String sessionId;

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}

package com.yolointerview.yolotest.dtos;

import com.yolointerview.yolotest.enums.MessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.socket.TextMessage;

@Getter
@Setter
@NoArgsConstructor
public class MessageDto<T> {

    private MessageType type;
    private String message;
    private T data;

    public MessageDto(MessageType type) {
        this.type = type;
    }

    public TextMessage asTextMessage() {
        return new TextMessage(MessageDtoConverter.convertToString(this));
    }
}

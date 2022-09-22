package com.yolointerview.yolotest.dtos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolointerview.yolotest.enums.MessageType;
import lombok.SneakyThrows;

public class MessageDtoConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public static String convertToString(MessageDto<?> messageDto) {
        return objectMapper.writeValueAsString(messageDto);
    }

    @SneakyThrows
    public static <T> MessageDto<T> convertToMessageDto(String payload) {
        return objectMapper.readValue(payload, new TypeReference<>() {
        });
    }

    @SneakyThrows
    public static <T> MessageDto<T> convertToMessageDto(String payload, Class<?> clazz) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(MessageDto.class, clazz);
        return objectMapper.readValue(payload, javaType);
    }

    public static MessageType getMessageType(String payload) {
        MessageDto<?> messageDto = convertToMessageDto(payload);
        return messageDto.getType();
    }
}

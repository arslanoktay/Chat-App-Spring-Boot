package com.oa.WebSockets.ChatApp.controller;

import com.oa.WebSockets.ChatApp.dto.ChatMessage;
import com.oa.WebSockets.ChatApp.dto.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller // Restcontroller değil!
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;

    // mesaj gönder
    @MessageMapping("/chat.sendChatMessage")
    public ChatMessage sendChatMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimeStamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        log.info("Sending chat message from: {}", chatMessage.getUserName());
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
        return chatMessage;
    }

    // kullanıcı ekle
    @MessageMapping("/chat.adduser")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getUserName());
        chatMessage.setMessageType(MessageType.JOIN);
        chatMessage.setMessage(chatMessage.getUserName() + " joined the chat");
        chatMessage.setTimeStamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        log.info("User joined: {}", chatMessage.getUserName());
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
        return chatMessage;
    }
}

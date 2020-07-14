package com.haapp.itchat.controller;

import com.haapp.itchat.model.ChatMessage;
import com.haapp.itchat.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
public class ChatController {

    private final ChatMessageService chatMessageService;

    public ChatController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessageService.addChatMessage(chatMessage);
        chatMessage = chatMessageService.getLastAddedMessage();
        return chatMessage;
    }

///*    @MessageMapping("/chat.addUser")
//    @SendTo("/topic/public")
//    public ChatMessage addUser(@Payload ChatMessage chatMessage,
//                               SimpMessageHeaderAccessor headerAccessor,
//                               Principal principal) {
//        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
//        List<ChatMessage> lastMessages = chatMessageService.getLastMessages();
//        lastMessages.stream().forEach((c) -> messagingTemplate.convertAndSendToUser(principal.getName(),"/queue/reply", c));
//        return chatMessage;
//    }
//
//    @MessageMapping("/chat.deleteMessage")
//    @SendTo("/topic/delete")
//    public ChatMessage deleteMessage(@Payload ChatMessage chatMessage) {
//        Long id = (Long) chatMessage.getId();
//        chatMessageService.deleteMessageById(id);
//        return chatMessage;
//    }
//
//    @MessageMapping("/chat.editMessage")
//    @SendTo("/topic/edit")
//    public ChatMessage editMessage(@Payload ChatMessage chatMessage) {
//        return chatMessageService.updateMessage(chatMessage);
//
//    }*/

}

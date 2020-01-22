package com.haapp.itchat.controller;

import com.haapp.itchat.model.ChatMessage;
import com.haapp.itchat.service.ChatMessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ChatController {

    private final ChatMessageService chatMessageService;

    public ChatController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    //@Payload parse from JSON to Object
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        //add message into db
        chatMessageService.addChatMessage(chatMessage);
        //send message to all connected clients
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        //add username in WS session. Can add or get headerAccessor properties
        chatMessageService.addChatMessage(chatMessage);
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
//todo добавить с topic/user вывод 20 сообщений при входе SendToUser получив идентификатор сессии
//todo возможно создать доп.модель для этого

}

package com.haapp.itchat.service;

import com.haapp.itchat.model.ChatMessage;
import com.haapp.itchat.repository.ChatMessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatMessageService {

    private final ChatMessageRepo chatMessageRepo;

    public ChatMessageService(ChatMessageRepo chatMessageRepo) {
        this.chatMessageRepo = chatMessageRepo;
    }

    @Transactional
    public void addChatMessage(ChatMessage chatMessage){
        this.chatMessageRepo.save(chatMessage);
    }
}

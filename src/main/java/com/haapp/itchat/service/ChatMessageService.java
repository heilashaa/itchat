package com.haapp.itchat.service;

import com.haapp.itchat.model.ChatMessage;
import com.haapp.itchat.repository.ChatMessageRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatMessageService {

    private final ChatMessageRepo chatMessageRepo;

    public ChatMessageService(ChatMessageRepo chatMessageRepo) {
        this.chatMessageRepo = chatMessageRepo;
    }

    public void addChatMessage(ChatMessage chatMessage){
        this.chatMessageRepo.save(chatMessage);
    }

    public List<ChatMessage> getLastMessages(){
        List<ChatMessage> chatMessages = chatMessageRepo.findTop10ByOrderByIdDesc().
                stream().
                sorted(Comparator.comparing(ChatMessage::getId)).
                collect(Collectors.toList());
        return chatMessages;
    }

    public ChatMessage getLastAddedMessage(){
        return chatMessageRepo.findFirstByOrderByIdDesc();
    }

    public void deleteMessageById(Long id) {
        Optional<ChatMessage> chatMessage = chatMessageRepo.findById(id);
        if(chatMessage.isPresent()){
            chatMessageRepo.deleteById(id);
        }
    }

    public ChatMessage updateMessage(ChatMessage chatMessage){
        return chatMessageRepo.save(chatMessage);
    }
}

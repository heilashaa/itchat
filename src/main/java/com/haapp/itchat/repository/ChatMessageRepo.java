package com.haapp.itchat.repository;

import com.haapp.itchat.model.ChatMessage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatMessageRepo extends CrudRepository<ChatMessage, Long> {

/*    @Query(value = "SELECT * FROM messages ORDER BY id DESC LIMIT ?1", nativeQuery = true)
    List<ChatMessage> getLimitMessagesOrderByIdDesc(String limit);*/
}

/*
    @Query(value = "SELECT * FROM messages ORDER BY id DESC LIMIT ?1", nativeQuery = true)
    List<Message> getLimitMessagesOrderByIdDesc(String limit);*/

package com.haapp.itchat.repository;

import com.haapp.itchat.model.ChatMessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepo extends CrudRepository<ChatMessage, Long> {

    List<ChatMessage> findTop10ByOrderByIdDesc();

    ChatMessage findFirstByOrderByIdDesc();
}


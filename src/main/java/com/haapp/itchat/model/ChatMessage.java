package com.haapp.itchat.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

//model to creat communicate between client and server
@Getter
@Setter
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private MessageType type;
    private String content;
    private String sender;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}

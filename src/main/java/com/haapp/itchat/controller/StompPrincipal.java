package com.haapp.itchat.controller;


import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class StompPrincipal implements Principal {

    private String name;

    public StompPrincipal(String name) {
        this.name = name;
    }

    public StompPrincipal() {
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}

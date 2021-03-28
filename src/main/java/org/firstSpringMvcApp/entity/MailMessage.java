package org.firstSpringMvcApp.entity;

import org.springframework.stereotype.Component;

@Component
public class MailMessage {
    private String message;
    private User user;
    public MailMessage(User user){
        this.user = user;
    }
    public User getUser(){
        return this.user;
    }
}

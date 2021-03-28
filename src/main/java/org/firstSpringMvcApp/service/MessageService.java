package org.firstSpringMvcApp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.firstSpringMvcApp.entity.MailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@Component
public class MessageService {
    ObjectMapper objectMapper;

    @Autowired
    JmsTemplate jmsTemplate;

    public void sendMailMessage(MailMessage message) throws Exception{
        String json = objectMapper.writeValueAsString(message);
        jmsTemplate.send("jms/queue/mail", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(json);
            }
        });
    }
}

package org.firstSpringMvcApp.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.firstSpringMvcApp.entity.MailMessage;
import org.firstSpringMvcApp.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.TextMessage;
import javax.mail.Message;

@Component
public class MailMessageListener {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    MessageService messageService;

    ObjectMapper objectMapper;

    @JmsListener(destination = "jms/queue/mail", concurrency = "10")
    public void onMailMessageReceived(Message message) throws Exception{
        logger.info("receive message: "+message);
        if(message instanceof TextMessage){
            String text = ((TextMessage) message).getText();
            MailMessage mailMessage = objectMapper.readValue(text, MailMessage.class);
            messageService.sendMailMessage(mailMessage);
        }else{
            logger.error("unable to process no-text message.");
        }
    }
}

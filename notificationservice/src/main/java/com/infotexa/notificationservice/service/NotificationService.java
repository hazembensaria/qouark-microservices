package com.infotexa.notificationservice.service;

import com.infotexa.notificationservice.model.Message;

import java.util.List;

public interface NotificationService {

   Message sendMessage(String fromUserUuid , String toEmail , String subject , String message);
   List<Message> getMessage(String userUuid);
   List<Message> getConversations(String userUuid , String conversationId);



}

package com.infotexa.notificationservice.repository;






import com.infotexa.notificationservice.model.Message;

import java.util.List;

public interface NotificationRepository {
    Message sendMessage(String fromUserUuid , String toEmail , String subject , String message);
    List<Message> getMessage(String userUuid);
    List<Message> getConversations(String userUuid , String conversationId);
    String getMessageStatus(String userUuid , Long messageId);
    String updateMessageStatus(String userUuid , Long messageId , String status);


}

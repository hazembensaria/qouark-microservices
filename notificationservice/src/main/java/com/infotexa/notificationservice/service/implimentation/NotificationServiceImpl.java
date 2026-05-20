package com.infotexa.notificationservice.service.implimentation;

import com.infotexa.notificationservice.model.Message;
import com.infotexa.notificationservice.repository.NotificationRepository;
import com.infotexa.notificationservice.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public Message sendMessage(String fromUserUuid, String toEmail, String subject, String message) {
        return notificationRepository.sendMessage(fromUserUuid, toEmail, subject, message);
    }

    @Override
    public List<Message> getMessage(String userUuid) {
         var messages = notificationRepository.getMessage(userUuid);
         messages.forEach(message ->
                 message.setStatus(
                         notificationRepository.getMessageStatus(Objects.equals(userUuid, message.getSenderUuid()) ? message.getSenderUuid() : message.getReceiverUuid(), message.getMessageId())
                 ));
            return messages;
    }

    @Override
    public List<Message> getConversations(String userUuid, String conversationId) {
        var messages = notificationRepository.getConversations(userUuid , conversationId);
        messages.forEach(message ->
                message.setStatus(
                        notificationRepository.updateMessageStatus(userUuid,message.getMessageId(), "READ")
                ));
        return messages;
    }
}

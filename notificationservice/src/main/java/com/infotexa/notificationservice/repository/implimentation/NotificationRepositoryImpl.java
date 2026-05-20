package com.infotexa.notificationservice.repository.implimentation;


import com.infotexa.notificationservice.exception.ApiException;
import com.infotexa.notificationservice.model.Message;
import com.infotexa.notificationservice.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.infotexa.notificationservice.query.NotificationQuery.*;
import static com.infotexa.notificationservice.utils.EmailUtils.randomUUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final JdbcClient jdbc;

    @Override
    public Message sendMessage(String fromUserUuid, String toEmail, String subject, String message) {
        try {
            return jdbc.sql(CREATE_MESSAGE_FUNCTION)
                    .params( Map.of("messageUuid" , randomUUID.get() , "fromUserUuid" , fromUserUuid , "toEmail" , toEmail , "subject" , subject , "message" , message , "conversationId" , conversationExist(fromUserUuid , toEmail) ? getConversationId(fromUserUuid , toEmail) : randomUUID.get()))
                    .query(Message.class).single();
        }catch (EmptyResultDataAccessException exception ){
            throw new ApiException(String.format("User with email %s not found", toEmail));
        } catch (Exception exception){
            throw new ApiException(exception.getMessage());

        }
    }

    @Override
    public List<Message> getMessage(String userUuid) {
        try {
            return jdbc.sql(SELECT_MESSAGES_QUERY)
                    .params( Map.of("userUuid" , userUuid))
                    .query(Message.class).list();
        }catch (EmptyResultDataAccessException exception ){
            throw new ApiException(String.format("User with userUuid %s not found", userUuid));
        } catch (Exception exception){
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public List<Message> getConversations(String userUuid, String conversationId) {
        try {
            return jdbc.sql(SELECT_MESSAGES_BY_CONVERSATION_ID_QUERY)
                    .params( Map.of("userUuid" , userUuid , "conversationId" , conversationId))
                    .query(Message.class).list();
        }catch (EmptyResultDataAccessException exception ){
            throw new ApiException(String.format("User with userUuid %s not found", userUuid));
        } catch (Exception exception){
            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public String getMessageStatus(String userUuid, Long messageId) {
        try {
            return jdbc.sql(SELECT_MESSAGE_STATUS_QUERY)
                    .params( Map.of("userUuid" , userUuid , "messageId" , messageId))
                    .query(String.class).single() ;
        }catch (EmptyResultDataAccessException exception ){
            throw new ApiException(String.format("User with userUuid %s not found", userUuid));
        } catch (Exception exception){

            throw new ApiException("An occurred. Please try again.");

        }
    }

    @Override
    public String updateMessageStatus(String userUuid, Long messageId, String status) {
        try {
             jdbc.sql(UPDATE_MESSAGE_STATUS_QUERY)
                    .params( Map.of("userUuid" , userUuid , "messageId" , messageId , "status" , status))
                    .update();
            return status;
        }catch (EmptyResultDataAccessException exception ){

            throw new ApiException(String.format("User with userUuid %s not found", userUuid));
        } catch (Exception exception){

            throw new ApiException("An occurred. Please try again.");

        }
    }


    private Boolean conversationExist(String fromUserUuid , String toEmail){
        try {
            var count =  jdbc.sql(SELECT_MESSAGE_COUNT_QUERY)
                    .params( Map.of("fromUserUuid" , fromUserUuid , "toEmail" , toEmail))
                    .query(Integer.class).single();
            return count > 0 ;
        }catch (EmptyResultDataAccessException exception ){

            throw new ApiException(String.format("User with email %s not found", toEmail));
        } catch (Exception exception){
            throw new ApiException("An occurred. Please try again.");

        }
    }

    private String getConversationId(String userUuid , String toEmail ){
        try {
            return jdbc.sql(SELECT_CONVERSATION_ID_QUERY)
                    .params( Map.of("userUuid" , userUuid , "toEmail" , toEmail))
                    .query(String.class).single();
        }catch (EmptyResultDataAccessException exception ){
            throw new ApiException(String.format("User with email %s not found", toEmail));
        } catch (Exception exception){
            throw new ApiException("An occurred. Please try again.");

        }
    }


}

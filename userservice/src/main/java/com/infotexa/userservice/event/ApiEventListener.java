package com.infotexa.userservice.event;

import com.infotexa.userservice.domain.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiEventListener {


    private final KafkaTemplate<String, Notification> kafkaTemplate;


     private static final String NOTIFICATION_TOPIC = "NOTIFICATION_TOPIC";
     @EventListener
    public void onApiEvent(Event event){
         try {
             var message = MessageBuilder
                     .withPayload(new Notification(event))
                     .setHeader(TOPIC, NOTIFICATION_TOPIC)
                     .build();
             this.kafkaTemplate.send(message);
         } catch (Exception e) {
             // don't break registration flow because of Kafka
             log.error("Failed to publish notification event", e);
         }
     }




}

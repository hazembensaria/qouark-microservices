package com.infotexa.notificationservice.resource;



import com.infotexa.notificationservice.domain.Response;
import com.infotexa.notificationservice.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;


import static com.infotexa.notificationservice.utils.RequestUtils.getResponse;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@AllArgsConstructor
@RequestMapping("/notification")
public class NotificationResource {
       private final NotificationService notificationService;

        @PostMapping("/messages")
        public ResponseEntity<Response>sendMessage(@NotNull Authentication authentication , HttpServletRequest request, @RequestParam(value = "toEmail") String toEmail, @RequestParam(value = "message") String message ){
            notificationService.sendMessage(authentication.getName() , toEmail , "Subject" , message);
            var messages = notificationService.getMessage(authentication.getName());
            return created(URI.create("")).body(getResponse(request , Map.of("messages" , messages) , "Message created" , CREATED));

        }

    @PostMapping("/reply")
    public ResponseEntity<Response>replyToMessage(@NotNull Authentication authentication , HttpServletRequest request, @RequestParam(value = "toEmail") String toEmail, @RequestParam(value = "message") String message ){
       var newMessage = notificationService.sendMessage(authentication.getName() , toEmail , "Subject" , message);
        return ok(getResponse(request , Map.of("message" , newMessage) , "Message sent" , OK));

    }

    @GetMapping("/messages")
    public ResponseEntity<Response>getMessages(@NotNull Authentication authentication , HttpServletRequest request ){
        var newMessage = notificationService.getMessage(authentication.getName());
        return ok(getResponse(request , Map.of("messages" , newMessage) , "Message retrieved" , OK));

    }

    @GetMapping("/messages/{conversationId}")
    public ResponseEntity<Response>getConversations(@NotNull Authentication authentication , HttpServletRequest request, @PathVariable String conversationId ){
        var conversation = notificationService.getConversations(authentication.getName() , conversationId);
        var messages = notificationService.getMessage(authentication.getName());
        return ok(getResponse(request , Map.of("conversation" , conversation , "messages" , messages) , "Message retrieved" , OK));

    }

}

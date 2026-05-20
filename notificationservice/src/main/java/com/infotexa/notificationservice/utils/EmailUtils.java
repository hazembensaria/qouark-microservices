package com.infotexa.notificationservice.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;
import java.util.function.Supplier;



public class EmailUtils {

    public static Supplier<String> randomUUID = () -> UUID.randomUUID().toString();

    public static String getVerificationUrl(String host , String token ){
        return host + "/verify/account?token=" + token;
    }

    public static String getResetPasswordUrl(String host , String token ){
        return host + "/verify/password?token=" + token;
    }

    public static String getTicketUrl(String host , String ticketNumber ){
        return host + "/tickets/" + ticketNumber;
    }
}

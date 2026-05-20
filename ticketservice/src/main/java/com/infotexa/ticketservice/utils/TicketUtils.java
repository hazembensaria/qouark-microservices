package com.infotexa.ticketservice.utils;

import com.infotexa.ticketservice.exception.ApiException;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;



public class TicketUtils {

    public static Supplier<String> randomUUID = () -> UUID.randomUUID().toString();

    public static String getFileUri(String fileUuid) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/storage/files/")
                .path(fileUuid)
                .toUriString();
    }
}

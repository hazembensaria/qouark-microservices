package com.infotexa.userservice.utils;

import com.infotexa.userservice.exception.ApiException;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.infotexa.userservice.consatant.Constant.INFOTEXA_LLC;
import static dev.samstevens.totp.util.Utils.getDataUriForImage;
import static org.apache.commons.lang.RandomStringUtils.randomNumeric;

public class UserUtils {

    public static Supplier<String> randomUUID = () -> UUID.randomUUID().toString();
    public static Supplier<String> memberId = () -> randomNumeric(4) + "-" + randomNumeric(2) + "-" + randomNumeric(4);

    public static Function<String, QrData> qrDataFunction = qrCodeSecret -> new QrData.Builder()
            .issuer(INFOTEXA_LLC)
            .label(INFOTEXA_LLC)
            .secret(qrCodeSecret)
            .algorithm(HashingAlgorithm.SHA1)
            .digits(6)
            .period(30)
            .build();

    public static Function<String, String> qrCodeImageUri = qrCodeSecret ->{

        try {
            var data = qrDataFunction.apply(qrCodeSecret);
            var generator = new ZxingPngQrGenerator();
            var imageData = generator.generate(data);
            return getDataUriForImage(imageData, generator.getImageMimeType());
        }catch (QrGenerationException exception){
            throw new ApiException("Failed to generate QR code image URI");
        }
    };

    public static Supplier<String> qrCodeSecret = () -> new DefaultSecretGenerator().generate();
}

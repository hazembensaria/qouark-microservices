package com.infotexa.authorizationserver.utils;

import com.infotexa.authorizationserver.model.User;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
@Slf4j
public class UserUtils {

//    public static boolean verifyQrCode(String secret , String code) {
//        TimeProvider timeProvider = new SystemTimeProvider();
//        CodeGenerator codeGenerator = new DefaultCodeGenerator();
//        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
//        return verifier.isValidCode(secret, code);
//    }


    public static boolean verifyQrCode(String secret, String code) {
        log.info("==== DEBUG TOTP VALIDATION ====");
        log.info("User QR secret: [{}]", secret);
        log.info("Is null? {}", (secret == null));
        log.info("Length: {}", (secret != null ? secret.length() : "-"));

        // Optional: Check if the secret is valid Base32
        try {
            Base32 base32 = new Base32();
            byte[] decoded = base32.decode(secret);
            log.info("Secret is valid Base32: decoded {} bytes.", decoded.length);
        } catch (Exception base32Ex) {
            log.error("Secret is NOT valid Base32: {}", base32Ex.getMessage());
        }

        try {
            int TIME_STEP_MS = 30_000;
            TimeProvider timeProvider = new SystemTimeProvider();
            CodeGenerator codeGenerator = new DefaultCodeGenerator();
            CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

            long nowMillis = System.currentTimeMillis();
            String codeCurrent = codeGenerator.generate(secret, nowMillis);

            log.info("Code from user: {}", code);
            log.info("Code expected (server now): {}", codeCurrent);

            // Codes for previous, current, and next time window (tolerance for clock skew)
            String codeMinus1 = codeGenerator.generate(secret, nowMillis - TIME_STEP_MS);
            String codePlus1 = codeGenerator.generate(secret, nowMillis + TIME_STEP_MS);
            log.info("Code -30s window: {}", codeMinus1);
            log.info("Code +30s window: {}", codePlus1);

            boolean manualMatch = code.equals(codeCurrent) || code.equals(codeMinus1) || code.equals(codePlus1);
            log.info("Manual match for time windows: {}", manualMatch);

            boolean result = verifier.isValidCode(secret, code);
            log.info("Verifier isValidCode result: {}", result);
            log.info("==== END DEBUG ====");
            return result;
        } catch (CodeGenerationException e) {
            log.error("Error generating TOTP code: {}", e.getMessage(), e);
            return false;
        }
    }


//    public static User getUser(Authentication authentication) {
//        if (authentication  instanceof OAuth2AuthorizationCodeRequestAuthenticationToken) {
//            var userNamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) authentication.getPrincipal();
//            return (User) userNamePasswordAuthenticationToken.getPrincipal();
//        }
//        return (User) authentication.getPrincipal();
//
//    }

    public static boolean verifyCode(String secret, String code) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        return verifier.isValidCode(secret, code);
    }

    public static User getUser(Authentication authentication) {
        Object principal = authentication;

        // Unwrap if wrapped in Authentication as principal
        while (principal instanceof Authentication innerAuth) {
            principal = innerAuth.getPrincipal();
        }

        if (principal instanceof User user) {
            return user;
        }
        // If your principal is something else, handle accordingly
        // Example: Spring's org.springframework.security.core.userdetails.User
        if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
            // You should map springUser.getUsername() to your User instance
            // Example: return userService.findByUsername(springUser.getUsername());
            throw new IllegalStateException("Principal is a Spring User -- load your own User by username");
        }
        if (principal instanceof String username) {
            // Principal might just be a username String
            // Example: return userService.findByUsername(username);
            throw new IllegalStateException("Principal is a String -- load your own User by username");
        }

        throw new IllegalStateException("Principal is not a User! Actual type: " + principal.getClass());
    }
}

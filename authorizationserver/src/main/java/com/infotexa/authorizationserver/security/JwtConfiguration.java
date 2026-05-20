package com.infotexa.authorizationserver.security;


import com.infotexa.authorizationserver.keyUtils.KeyUtils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
@RequiredArgsConstructor
public class JwtConfiguration {
    private final KeyUtils keyUtils;

    @Bean
    public JwtDecoder jwtDecoder() throws JOSEException {
        return NimbusJwtDecoder.withPublicKey(keyUtils.getRsaKey().toRSAPublicKey()).build();
    }

    @Bean
    JWKSource<SecurityContext> jwkSource(){
        RSAKey rsaKey = keyUtils.getRsaKey();
        JWKSet set = new JWKSet(rsaKey);
        return (j ,sc ) -> j.select(set);

    }




}

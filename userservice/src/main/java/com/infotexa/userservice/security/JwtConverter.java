package com.infotexa.userservice.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;


@Component
public class JwtConverter implements Converter<Jwt, JwtAuthenticationToken> {

    private final static String AUTHORITIES_KEY = "authorities";
    @Override
    public JwtAuthenticationToken convert(Jwt jwt) {
        var claims = (String) jwt.getClaims().get(AUTHORITIES_KEY);
        var authorities = commaSeparatedStringToAuthorityList(claims);
        return new JwtAuthenticationToken(jwt, authorities , jwt.getSubject());
    }
}

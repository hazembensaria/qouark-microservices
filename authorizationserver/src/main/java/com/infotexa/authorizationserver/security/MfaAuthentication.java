package com.infotexa.authorizationserver.security;

import lombok.Getter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@Getter
public class MfaAuthentication extends AnonymousAuthenticationToken {
    private final Authentication primaryAuthentiacation;
    public MfaAuthentication(Authentication authentication , String authority) {
        super("anonymous", "anonymous", createAuthorityList("ROLE_ANONYMOUS" , authority));
        this.primaryAuthentiacation = authentication;
    }

    @Override
    public Object getPrincipal() {
        return this.primaryAuthentiacation ;
    }
}

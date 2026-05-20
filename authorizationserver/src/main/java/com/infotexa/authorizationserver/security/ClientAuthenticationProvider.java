package com.infotexa.authorizationserver.security;

import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class ClientAuthenticationProvider implements AuthenticationProvider {

    private final RegisteredClientRepository registeredClientRepository;
    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var clientRefreshTokenAuthentication = (ClientRefreshTokenAuthentication) authentication;
        if (!ClientAuthenticationMethod.NONE.equals(clientRefreshTokenAuthentication.getClientAuthenticationMethod())) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_CLIENT, "Client authentication method not supported: " + clientRefreshTokenAuthentication.getClientAuthenticationMethod(), null));
        }
        var clientId = clientRefreshTokenAuthentication.getPrincipal().toString();
        var registeredClient = registeredClientRepository.findByClientId(clientId);
        if (registeredClient == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_CLIENT, "Client not valid: " + clientId, null));
        }
        if(!registeredClient.getClientAuthenticationMethods().contains(clientRefreshTokenAuthentication.getClientAuthenticationMethod())){
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_CLIENT, "Client authentication method not supported: " + clientRefreshTokenAuthentication.getClientAuthenticationMethod(), null));
        }
        return new ClientRefreshTokenAuthentication(registeredClient);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ClientRefreshTokenAuthentication.class.isAssignableFrom(authentication);
    }
}

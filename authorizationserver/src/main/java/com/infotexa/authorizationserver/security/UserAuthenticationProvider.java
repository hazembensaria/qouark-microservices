package com.infotexa.authorizationserver.security;


import com.infotexa.authorizationserver.exception.ApiException;
import com.infotexa.authorizationserver.model.User;
import com.infotexa.authorizationserver.service.UserService;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated;
import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;

@Component
@AllArgsConstructor
public class UserAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final BCryptPasswordEncoder encoder;


    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
       try {
           var user = userService.getUserByEmail((String) authentication.getPrincipal());
           validateUser.accept(user);
           if(encoder.matches((String) authentication.getCredentials(), user.getPassword())) {
               return authenticated(user , "[PROTECTED]" , commaSeparatedStringToAuthorityList(user.getRole() + "," + user.getAuthorities()));
           }
           else throw new BadCredentialsException("Invalid Email/Password. Please Try Again.");
       }catch (BadCredentialsException | ApiException | LockedException | CredentialsExpiredException |
               DisabledException exception){
           throw new ApiException(exception.getMessage());
       } catch (Exception exception){
           throw new ApiException("An error occurred while authenticating the user. Please try again later.");
       }
    }

    @Override
    public boolean supports(Class<?> authenticationType) {
        return authenticationType.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }

    private final Consumer<User> validateUser = user -> {
        if(!user.isAccountNonLocked() || user.getLoginAttempts() >= 5){
            throw new LockedException(String.format(user.getLoginAttempts() > 0 ?"Account currently Locked after %s login attempts" : "Account currently Locked" , user.getLoginAttempts()));
        }
        if(!user.isEnabled() ){
            throw new DisabledException("Account is disabled");
        }
        if(!user.isAccountNonExpired() ){
            throw new DisabledException("Account is expired , please contact administration");
        }
    };
}

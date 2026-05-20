package com.infotexa.authorizationserver.controller;


import com.infotexa.authorizationserver.exception.ApiException;
import com.infotexa.authorizationserver.model.User;
import com.infotexa.authorizationserver.security.MfaAuthentication;
import com.infotexa.authorizationserver.service.UserService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

import static com.infotexa.authorizationserver.utils.RequestUtils.getMessage;
import static com.infotexa.authorizationserver.utils.UserUtils.getUser;

@Controller
@AllArgsConstructor
public class LoginController {

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler("/mfa?error");
    private final UserService userService;


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/mfa")
    public String mfa(Model modal, @CurrentSecurityContext SecurityContext context) {
        modal.addAttribute("email", getAuthenticatedUser(context.getAuthentication()));
        return "mfa";
    }

//    @PostMapping("/mfa")
//    public void validationCode(@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response, @CurrentSecurityContext SecurityContext context) throws ServletException, IOException {
//        var user = getUser(context.getAuthentication());
//        if (userService.verifyQrCode(user.getUserUuid(), code)) {
//            this.authenticationSuccessHandler.onAuthenticationSuccess(request, response, getAuthentication(request , response));
//            return;
//        }
//        this.authenticationFailureHandler.onAuthenticationFailure(request, response, new BadCredentialsException("Invalid code"));
//    }

    @PostMapping("/mfa")
    public void validationCode(@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response, @CurrentSecurityContext SecurityContext context) throws ServletException, IOException {
        var user = getUser(context.getAuthentication());

        if (userService.verifyQrCode(user.getUserUuid(), code)) {
            // 2. Get the new, FULLY authenticated token
            Authentication fullyAuthenticatedUser = getAuthentication(request, response);

            // 3. Update the Security Context
            SecurityContextHolder.getContext().setAuthentication(fullyAuthenticatedUser);

            // 4. CRITICAL: Save the context to the HTTP Session!
            securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);

            // 5. Now it is safe to redirect
            this.authenticationSuccessHandler.onAuthenticationSuccess(request, response, fullyAuthenticatedUser);
            return;
        }

        this.authenticationFailureHandler.onAuthenticationFailure(request, response, new BadCredentialsException("Invalid code"));
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }

    @GetMapping("/error")
    public String logout(HttpServletRequest request, HttpServletResponse response, Model model) {
        var errorException = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (errorException instanceof ApiException || errorException instanceof BadCredentialsException) {
            request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorException);
            return  "login";
        }
        model.addAttribute("message", getMessage(request));
        return "error";
    }

    private String getAuthenticatedUser(Authentication authentication) {
        Object principal = authentication;
        // Unwrap iteratively if anyone is still wrapping
        while (principal instanceof Authentication authWrap) {
            principal = authWrap.getPrincipal();
        }

        if (principal instanceof User user) {
            return user.getEmail();
        } else if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
            return springUser.getUsername();
        } else if (principal instanceof String username) {
            return username;
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }
    }

    private Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {

            SecurityContext securityContext = SecurityContextHolder.getContext();
            MfaAuthentication mfaAuthentication = (MfaAuthentication) securityContext.getAuthentication();
            securityContext.setAuthentication(mfaAuthentication);
            SecurityContextHolder.setContext(securityContext);
            securityContextRepository.saveContext(securityContext, request, response);
            return mfaAuthentication.getPrimaryAuthentiacation();

    }

//    private Object getAuthenticatedUser(Authentication authentication) {
//
//        return ((User) authentication.getPrincipal()).getEmail();
//    }

//    private String getAuthenticatedUser(Authentication authentication) {
//        Object principal = authentication.getPrincipal();
//        if (principal instanceof User user) {
//            return user.getEmail();
//        } else if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
//            // If using default Spring Security principal
//            return springUser.getUsername();
//        } else if (principal instanceof String username) {
//            // Sometimes principal is just the username (String)
//            return username;
//        } else {
//            // Fallback for unexpected principal types
//            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
//        }
//    }

}

package com.infotexa.discoveryservice.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static com.infotexa.discoveryservice.constant.Roles.APP_READ;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class FilterChainConfig {

    private final DiscoveryUserDetailsService userDetailsService ;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http ) throws Exception {
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/eureka/**"))
                .userDetailsService(userDetailsService)
                .exceptionHandling(exception -> exception.accessDeniedHandler(new DiscoveryAccessDeniedHandler()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/eureka/fonts/**" , "/eureka/css/**" , "/eureka/js/**" , "/eureka/images/**" , "/icon/**").permitAll()
                        .requestMatchers("/eureka/**").hasAuthority(APP_READ)
                        .requestMatchers("/**").hasAnyAuthority(APP_READ)
                        .anyRequest().authenticated())
                .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(new DiscoveryAuthenticationEntryPoint()));

        return http.build();
    }


}

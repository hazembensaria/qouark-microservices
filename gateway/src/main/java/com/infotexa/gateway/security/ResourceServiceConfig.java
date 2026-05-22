package com.infotexa.gateway.security;


import com.infotexa.gateway.handler.GatewayAccessDeniedHandler;
import com.infotexa.gateway.handler.GatewayAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ResourceServiceConfig {

    @Value("${jwks.uri}")
    private String jwkSetUri ;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http ) throws Exception {
        log.info("Configuring security filter chain for resource server with JWKS URI: {}", jwkSetUri);
            http.csrf(AbstractHttpConfigurer::disable)
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/register/**" , "/user/verify/account/**" ,"/user/verify/password/**" , "/user/resetpassword/**" , "/user/image/**" , "/authorization/**").permitAll()
                        .anyRequest().authenticated())
                    .oauth2ResourceServer(oauth2 -> oauth2
                            .accessDeniedHandler(new GatewayAccessDeniedHandler())
                            .authenticationEntryPoint(new GatewayAuthenticationEntryPoint())
                            .jwt(jwt -> jwt.jwkSetUri(jwkSetUri).jwtAuthenticationConverter(new JwtConverter())));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200" , "http://localhost:3000"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("File-Name", "Authorization", "Content-Type", "Accept", "Origin"));
        corsConfiguration.setExposedHeaders(List.of("File-Name","Authorization"));

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }



}

package com.infotexa.ticketservice.security;




import com.infotexa.ticketservice.handler.CustomAccessDeniedHandler;
import com.infotexa.ticketservice.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ResourceServiceConfig {

    @Value("${jwks.uri}")
    private String jwkSetUri ;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http ) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
//                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/regester/**" , "/user/verify/account/**", "/user/verify/password/**" , "/user/reset_password/**" , "/user/image/**" ).permitAll()
                        .anyRequest().authenticated())
                    .oauth2ResourceServer(oauth2 -> oauth2
                            .accessDeniedHandler(new CustomAccessDeniedHandler())
                            .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                            .jwt(jwt -> jwt.jwkSetUri(jwkSetUri).jwtAuthenticationConverter(new JwtConverter())));
        return http.build();
    }

//    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin"));
        corsConfiguration.setExposedHeaders(List.of("Authorization"));

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }



}

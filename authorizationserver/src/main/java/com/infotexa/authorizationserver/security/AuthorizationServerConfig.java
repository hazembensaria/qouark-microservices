package com.infotexa.authorizationserver.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.oauth2.server.authorization.OAuth2TokenType.ACCESS_TOKEN;

@Configuration
@RequiredArgsConstructor
public class AuthorizationServerConfig {
    private final JwtConfiguration jwtConfiguration ;

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http,
            RegisteredClientRepository registeredClientRepository
    ) throws Exception {
        var authorizationConfig = new OAuth2AuthorizationServerConfigurer();

        http
                .securityMatcher(authorizationConfig.getEndpointsMatcher())
                .with(authorizationConfig, configurer -> {
                    configurer
                            .oidc(Customizer.withDefaults())
                            .authorizationServerSettings(authorizationServerSettings())
                            .registeredClientRepository(registeredClientRepository)
                            .tokenGenerator(tokenGenerator())
                            .tokenEndpoint(tokenEndpoint -> tokenEndpoint
                                    .accessTokenRequestConverter(new ClientRefreshTokenAuthenticationConverter())
                                    .authenticationProvider(new ClientAuthenticationProvider(registeredClientRepository))
                            );
                })
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exception -> exception.defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint("/login"),
                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                ));

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login").permitAll()
                .requestMatchers("/logout").permitAll()
                .requestMatchers(POST, "/logout").permitAll()
                .requestMatchers("/mfa").hasAuthority("MFA_REQUIRED")
                .anyRequest().authenticated());

        http.formLogin(form -> form.loginPage("/login")
                .successHandler(new MfaAuthenticationHandler("/mfa", "MFA_REQUIRED"))
                .failureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error")));

        http.logout(logout -> logout
                .logoutSuccessUrl("http://localhost:3000")
                .addLogoutHandler(new CookieClearingLogoutHandler("JSESSIONID")));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200" , "http://localhost:3000"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin"));
        corsConfiguration.setExposedHeaders(List.of("Authorization"));

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }


    @Bean
    public OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator(){
        var jwtGenerator = UserJwtGenerator.init(new NimbusJwtEncoder(jwtConfiguration.jwkSource()));
        jwtGenerator.setJwtCustomizer(customizer());
        OAuth2TokenGenerator<OAuth2RefreshToken> refreshTokenOAuth2TokenGenerator = new ClientOauth2RefreshTokenGenerator() ;
        return new DelegatingOAuth2TokenGenerator(jwtGenerator, refreshTokenOAuth2TokenGenerator);
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new SavedRequestAwareAuthenticationSuccessHandler();
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings(){
        return AuthorizationServerSettings.builder().build();
    }


    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> customizer() {
        return context -> {
            if (ACCESS_TOKEN.equals(context.getTokenType())) {
                context.getClaims().claims(claims -> claims.put("authorities" , getAuthorities(context)));
            }
        };
    }

    private  String getAuthorities(JwtEncodingContext context){
       return context.getPrincipal().getAuthorities().stream()
               .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }
}

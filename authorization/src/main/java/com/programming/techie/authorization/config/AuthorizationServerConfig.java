package com.programming.techie.authorization.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.programming.techie.authorization.jose.Jwks;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationValidator;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Slf4j
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {
  private final JwtConfigProperties jwtConfigProperties;

  @Value("${app.authorization}")
  private String authorizationHost;

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {

    // Replaced this call with the implementation of applyDefaultSecurity() to be able to add a custom redirect_uri validator
    // OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

    OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
            new OAuth2AuthorizationServerConfigurer();

    // Register a custom redirect_uri validator, that allows redirect uris based on https://localhost during development
    authorizationServerConfigurer
            .authorizationEndpoint(authorizationEndpoint ->
                    authorizationEndpoint
                            .authenticationProviders(configureAuthenticationValidator())
            );

    RequestMatcher endpointsMatcher = authorizationServerConfigurer
            .getEndpointsMatcher();

    http
            .securityMatcher(endpointsMatcher)
            .authorizeHttpRequests(authorize ->
                    authorize.anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
            .apply(authorizationServerConfigurer);

    http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
            .oidc(Customizer.withDefaults()); // Enable OpenID Connect 1.0

    http
            .exceptionHandling(exceptions ->
                    exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
            )
            .oauth2ResourceServer(resource -> resource.jwt(Customizer.withDefaults()));

    return http.build();
  }

  @Bean
  public RegisteredClientRepository registeredClientRepository() {
    RegisteredClient writerClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("client")
            .clientSecret("{noop}secret")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .redirectUri("https://localhost:8443/openapi/webjars/swagger-ui/oauth2-redirect.html")
            .scope(OidcScopes.OPENID)
            .scope("read")
            .scope("write")
            .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
            .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofHours(12)).build())
            .build();

    return new InMemoryRegisteredClientRepository(writerClient);
  }

  public JWKSource<SecurityContext> jwkSource() {
    RSAKey rsaKey = Jwks.generateRsa(jwtConfigProperties.getPublicKey(), jwtConfigProperties.getPrivateKey());
    JWKSet jwkSet = new JWKSet(rsaKey);
    return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource());
  }

  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder().issuer("http://" + authorizationHost + ":9999").build();
  }

  private Consumer<List<AuthenticationProvider>> configureAuthenticationValidator() {
    return (authenticationProviders) ->
            authenticationProviders.forEach((authenticationProvider) -> {
              if (authenticationProvider instanceof OAuth2AuthorizationCodeRequestAuthenticationProvider) {
                Consumer<OAuth2AuthorizationCodeRequestAuthenticationContext> authenticationValidator =
                        // Override default redirect_uri validator
                        new CustomRedirectUriValidator()
                                // Reuse default scope validator
                                .andThen(OAuth2AuthorizationCodeRequestAuthenticationValidator.DEFAULT_SCOPE_VALIDATOR);

                ((OAuth2AuthorizationCodeRequestAuthenticationProvider) authenticationProvider)
                        .setAuthenticationValidator(authenticationValidator);
              }
            });
  }

  static class CustomRedirectUriValidator implements Consumer<OAuth2AuthorizationCodeRequestAuthenticationContext> {
    @Override
    public void accept(OAuth2AuthorizationCodeRequestAuthenticationContext authenticationContext) {
      OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication =
              authenticationContext.getAuthentication();
      RegisteredClient registeredClient = authenticationContext.getRegisteredClient();
      String requestedRedirectUri = authorizationCodeRequestAuthentication.getRedirectUri();

      log.trace("Will validate the redirect uri {}", requestedRedirectUri);

      // Use exact string matching when comparing client redirect URIs against pre-registered URIs
      if (!registeredClient.getRedirectUris().contains(requestedRedirectUri)) {
        log.trace("Redirect uri is invalid!");
        OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST);
        throw new OAuth2AuthorizationCodeRequestAuthenticationException(error, null);
      }
      log.trace("Redirect uri is OK!");
    }
  }
}

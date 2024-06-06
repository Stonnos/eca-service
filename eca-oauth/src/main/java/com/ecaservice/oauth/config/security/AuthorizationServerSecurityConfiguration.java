package com.ecaservice.oauth.config.security;

import com.ecaservice.oauth.config.Oauth2RegisteredClient;
import com.ecaservice.oauth.config.TfaConfig;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.service.tfa.TfaCodeService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

/**
 * Authorization server security configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class AuthorizationServerSecurityConfiguration {

    private static final int AUTHORIZATION_SERVER_SECURITY_FILTER_ORDER = 0;

    private static final String OAUTH2_REGISTERED_CLIENTS_JSON = "oauth2-registered-clients.json";

    private final ObjectMapper objectMapper;
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /**
     * Creates oauth2 registered clients repository.
     *
     * @return oauth2 registered clients repository
     * @throws IOException in case of I/O error
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() throws IOException {
        List<Oauth2RegisteredClient> oauth2RegisteredClients = loadOauth2RegisteredClients();
        List<RegisteredClient> registeredClients = oauth2RegisteredClients
                .stream()
                .map(this::registeredClient)
                .toList();
        return new InMemoryRegisteredClientRepository(registeredClients);
    }

    /**
     * Creates oauth2 authorization service bean with database storage.
     *
     * @param jdbcTemplate               - jdbc template
     * @param registeredClientRepository - registered client repository
     * @return oauth2 authorization service
     */
    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService(JdbcTemplate jdbcTemplate,
                                                                 RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }

    /**
     * Creates authorization server security filter chain
     *
     * @param http                   - http security
     * @param authenticationProvider - password authentication provider
     * @return security filter chain
     * @throws Exception in case of error
     */
    @Bean
    @Order(AUTHORIZATION_SERVER_SECURITY_FILTER_ORDER)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      Oauth2PasswordGrantAuthenticationProvider authenticationProvider)
            throws Exception {
        var authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        authorizationServerConfigurer
                .tokenEndpoint(tokenEndpoint ->
                        tokenEndpoint
                                .accessTokenRequestConverter(new Oauth2PasswordGrantAuthenticationConverter())
                                .authenticationProvider(authenticationProvider)
                                .errorResponseHandler(new OAuth2AuthenticationFailureErrorHandler())
                );
        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
        http
                .cors(AbstractHttpConfigurer::disable)
                .securityMatcher(endpointsMatcher)
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .with(authorizationServerConfigurer, Customizer.withDefaults());
        return http.build();
    }

    /**
     * Creates password encoder bean.
     *
     * @return password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Creates password grant authentication provider.
     *
     * @param daoAuthenticationProvider  - dao authentication provider
     * @param oAuth2AuthorizationService - oauth2 authorization service
     * @param tfaConfig                  - tfa config
     * @param tfaCodeService             - tfa code service
     * @param userEntityRepository       - user entity repository
     * @param applicationEventPublisher  - application event publisher
     * @return password grant authentication provider
     */
    @Bean
    public Oauth2PasswordGrantAuthenticationProvider passwordGrantAuthenticationProvider(
            AuthenticationProvider daoAuthenticationProvider,
            OAuth2AuthorizationService oAuth2AuthorizationService,
            TfaConfig tfaConfig,
            TfaCodeService tfaCodeService,
            UserEntityRepository userEntityRepository,
            ApplicationEventPublisher applicationEventPublisher) {
        var oAuth2AccessTokenGenerator = new OAuth2AccessTokenGenerator();
        oAuth2AccessTokenGenerator.setAccessTokenCustomizer(new OAuth2TokenCustomizerImpl());
        return new Oauth2PasswordGrantAuthenticationProvider(oAuth2AuthorizationService, daoAuthenticationProvider,
                oAuth2AccessTokenGenerator, new OAuth2RefreshTokenGenerator(), tfaConfig, tfaCodeService,
                userEntityRepository, applicationEventPublisher);
    }

    /**
     * Creates dao authentication provider.
     *
     * @param passwordEncoder    - password encoder
     * @param userDetailsService - user details service
     * @return dao authentication provider
     */
    @Bean
    public AuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder,
                                                            UserDetailsService userDetailsService) {
        var daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    private List<Oauth2RegisteredClient> loadOauth2RegisteredClients() throws IOException {
        log.info("Starting to load oauth2 registered clients");
        var resource = resolver.getResource(OAUTH2_REGISTERED_CLIENTS_JSON);
        @Cleanup var inputStream = resource.getInputStream();
        List<Oauth2RegisteredClient> clients = objectMapper.readValue(inputStream, new TypeReference<>() {
        });
        log.info("[{}] oauth2 registered clients has been loaded", clients.size());
        return clients;
    }

    private RegisteredClient registeredClient(Oauth2RegisteredClient oauth2RegisteredClient) {
        return RegisteredClient.withId(oauth2RegisteredClient.getClientId())
                .clientId(oauth2RegisteredClient.getClientId())
                .clientName(oauth2RegisteredClient.getClientId())
                .clientSecret(oauth2RegisteredClient.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantTypes(authorizationGrantTypes -> {
                    List<AuthorizationGrantType> grantTypes =
                            oauth2RegisteredClient.getAuthorizationGrantTypes()
                                    .stream()
                                    .map(AuthorizationGrantType::new)
                                    .toList();
                    authorizationGrantTypes.addAll(grantTypes);
                })
                .scopes(scopes -> scopes.addAll(oauth2RegisteredClient.getScopes()))
                .tokenSettings(
                        TokenSettings.builder()
                                .accessTokenFormat(OAuth2TokenFormat.REFERENCE)
                                .accessTokenTimeToLive(Duration.ofMinutes(
                                        oauth2RegisteredClient.getAccessTokenTimeToLiveMinutes()))
                                .refreshTokenTimeToLive(Duration.ofMinutes(
                                        oauth2RegisteredClient.getRefreshTokenTimeToLiveMinutes()))
                                .build())
                .build();
    }
}

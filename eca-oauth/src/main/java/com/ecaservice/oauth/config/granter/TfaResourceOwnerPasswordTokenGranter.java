package com.ecaservice.oauth.config.granter;

import com.ecaservice.oauth.config.TfaConfig;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.event.model.TfaCodeNotificationEvent;
import com.ecaservice.oauth.exception.PasswordExpiredException;
import com.ecaservice.oauth.exception.TfaRequiredException;
import com.ecaservice.oauth.repository.UserEntityRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;

import java.time.LocalDateTime;

/**
 * Resource owner password granter implementation for two factor authentication support.
 *
 * @author Roman Batygin
 */
public class TfaResourceOwnerPasswordTokenGranter extends ResourceOwnerPasswordTokenGranter {

    private final TfaConfig tfaConfig;
    private final AuthorizationCodeServices authorizationCodeServices;
    private final UserEntityRepository userEntityRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Constructor with spring dependency injection.
     *
     * @param authenticationManager     - authentication manager
     * @param endpoints                 - authorization server endpoints configurer bean
     * @param tfaConfig                 - tfa config bean
     * @param authorizationCodeServices - authorization code services
     * @param userEntityRepository      - user entity repository bean
     * @param applicationEventPublisher - application event publisher bean
     */
    public TfaResourceOwnerPasswordTokenGranter(AuthenticationManager authenticationManager,
                                                AuthorizationServerEndpointsConfigurer endpoints,
                                                TfaConfig tfaConfig,
                                                AuthorizationCodeServices authorizationCodeServices,
                                                UserEntityRepository userEntityRepository,
                                                ApplicationEventPublisher applicationEventPublisher) {
        super(authenticationManager, endpoints.getTokenServices(), endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory());
        this.tfaConfig = tfaConfig;
        this.authorizationCodeServices = authorizationCodeServices;
        this.userEntityRepository = userEntityRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    protected OAuth2AccessToken getAccessToken(ClientDetails client, TokenRequest tokenRequest) {
        OAuth2Authentication oAuth2Authentication = getOAuth2Authentication(client, tokenRequest);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                (UsernamePasswordAuthenticationToken) oAuth2Authentication.getUserAuthentication();
        UserEntity userEntity = userEntityRepository.findUser(usernamePasswordAuthenticationToken.getName());
        checkPasswordExpiration(userEntity);
        if (Boolean.TRUE.equals(tfaConfig.getEnabled()) && userEntity.isTfaEnabled()) {
            String code = authorizationCodeServices.createAuthorizationCode(oAuth2Authentication);
            applicationEventPublisher.publishEvent(new TfaCodeNotificationEvent(this, userEntity, code));
            throw new TfaRequiredException(tfaConfig.getCodeValiditySeconds());
        }
        return getTokenServices().createAccessToken(oAuth2Authentication);
    }

    private void checkPasswordExpiration(UserEntity userEntity) {
        if (userEntity.getPasswordExpiredAt() != null &&
                userEntity.getPasswordExpiredAt().isBefore(LocalDateTime.now())) {
            throw new PasswordExpiredException();
        }
    }
}

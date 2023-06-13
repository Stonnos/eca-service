package com.ecaservice.oauth.config.granter;

import com.ecaservice.oauth.config.TfaConfig;
import com.ecaservice.oauth.event.model.TfaCodeNotificationEvent;
import com.ecaservice.oauth.exception.ChangePasswordRequiredException;
import com.ecaservice.oauth.exception.TfaRequiredException;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.service.tfa.TfaCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;

/**
 * Resource owner password granter implementation for two factor authentication support.
 *
 * @author Roman Batygin
 */
@Slf4j
public class TfaResourceOwnerPasswordTokenGranter extends ResourceOwnerPasswordTokenGranter {

    private final TfaConfig tfaConfig;
    private final TfaCodeService tfaCodeService;
    private final UserEntityRepository userEntityRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Constructor with spring dependency injection.
     *
     * @param authenticationManager     - authentication manager
     * @param endpoints                 - authorization server endpoints configurer bean
     * @param tfaConfig                 - tfa config bean
     * @param tfaCodeService            - tfa code service
     * @param userEntityRepository      - user entity repository bean
     * @param applicationEventPublisher - application event publisher bean
     */
    public TfaResourceOwnerPasswordTokenGranter(AuthenticationManager authenticationManager,
                                                AuthorizationServerEndpointsConfigurer endpoints,
                                                TfaConfig tfaConfig,
                                                TfaCodeService tfaCodeService,
                                                UserEntityRepository userEntityRepository,
                                                ApplicationEventPublisher applicationEventPublisher) {
        super(authenticationManager, endpoints.getTokenServices(), endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory());
        this.tfaConfig = tfaConfig;
        this.tfaCodeService = tfaCodeService;
        this.userEntityRepository = userEntityRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    protected OAuth2AccessToken getAccessToken(ClientDetails client, TokenRequest tokenRequest) {
        var oAuth2Authentication = getOAuth2Authentication(client, tokenRequest);
        var authentication = oAuth2Authentication.getUserAuthentication();
        var userEntity = userEntityRepository.findUser(authentication.getName());
        if (userEntity.isForceChangePassword()) {
            log.info("Password must be change for user [{}]", userEntity.getLogin());
            throw new ChangePasswordRequiredException();
        }
        if (Boolean.TRUE.equals(tfaConfig.getEnabled()) && userEntity.isTfaEnabled()) {
            log.info("Tfa required for user [{}]. Starting to sent authorization code", userEntity.getLogin());
            var tfaCodeModel = tfaCodeService.createAuthorizationCode(oAuth2Authentication);
            applicationEventPublisher.publishEvent(
                    new TfaCodeNotificationEvent(this, userEntity, tfaCodeModel.getCode()));
            throw new TfaRequiredException(tfaCodeModel.getToken(), tfaConfig.getCodeValiditySeconds());
        }
        return getTokenServices().createAccessToken(oAuth2Authentication);
    }
}

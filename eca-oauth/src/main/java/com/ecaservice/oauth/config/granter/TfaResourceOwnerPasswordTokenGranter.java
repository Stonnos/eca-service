package com.ecaservice.oauth.config.granter;

import com.ecaservice.oauth.config.TfaConfig;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.exception.TfaRequiredException;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.service.mail.NotificationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;

/**
 * Resource owner password granter implementation for two factor authentication support.
 *
 * @author Roman Batygin
 */
public class TfaResourceOwnerPasswordTokenGranter extends ResourceOwnerPasswordTokenGranter {

    private final TfaConfig tfaConfig;
    private final UserEntityRepository userEntityRepository;
    private final NotificationService notificationService;
    private final AuthorizationCodeServices authorizationCodeServices;

    /**
     * Constructor with spring dependency injection.
     *
     * @param authenticationManager     - authentication manager
     * @param endpoints                 - authorization server endpoints configurer bean
     * @param tfaConfig                 - tfa config bean
     * @param userEntityRepository      - user entity repository
     * @param notificationService       - notification service
     * @param authorizationCodeServices - authorization code services
     */
    public TfaResourceOwnerPasswordTokenGranter(AuthenticationManager authenticationManager,
                                                AuthorizationServerEndpointsConfigurer endpoints,
                                                TfaConfig tfaConfig,
                                                UserEntityRepository userEntityRepository,
                                                NotificationService notificationService,
                                                AuthorizationCodeServices authorizationCodeServices) {
        super(authenticationManager, endpoints.getTokenServices(), endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory());
        this.tfaConfig = tfaConfig;
        this.userEntityRepository = userEntityRepository;
        this.notificationService = notificationService;
        this.authorizationCodeServices = authorizationCodeServices;
    }

    @Override
    protected OAuth2AccessToken getAccessToken(ClientDetails client, TokenRequest tokenRequest) {
        OAuth2Authentication oAuth2Authentication = getOAuth2Authentication(client, tokenRequest);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                (UsernamePasswordAuthenticationToken) oAuth2Authentication.getUserAuthentication();
        UserEntity userEntity = userEntityRepository.findUser(usernamePasswordAuthenticationToken.getName());
        if (userEntity.isTfaEnabled()) {
            String code = authorizationCodeServices.createAuthorizationCode(oAuth2Authentication);
            notificationService.sendTfaCode(userEntity, code);
            throw new TfaRequiredException(tfaConfig.getCodeValiditySeconds());
        }
        return getTokenServices().createAccessToken(oAuth2Authentication);
    }
}

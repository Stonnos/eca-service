package com.ecaservice.oauth.config.granter;

import com.ecaservice.oauth.exception.TfaRequiredException;
import com.ecaservice.oauth.repository.UserEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

/**
 * Resource owner password granter implementation for two factor authentication support.
 *
 * @author Roman Batygin
 */
@Slf4j
public class TfaResourceOwnerPasswordTokenGranter extends ResourceOwnerPasswordTokenGranter {

    private final UserEntityRepository userEntityRepository;
    private final AuthorizationCodeServices authorizationCodeServices;

    public TfaResourceOwnerPasswordTokenGranter(
            AuthenticationManager authenticationManager,
            AuthorizationServerTokenServices tokenServices,
            ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory,
            UserEntityRepository userEntityRepository,
            AuthorizationCodeServices authorizationCodeServices) {
        super(authenticationManager, tokenServices, clientDetailsService, requestFactory);
        this.userEntityRepository = userEntityRepository;
        this.authorizationCodeServices = authorizationCodeServices;
    }

    @Override
    protected OAuth2AccessToken getAccessToken(ClientDetails client, TokenRequest tokenRequest) {
        OAuth2Authentication oAuth2Authentication = getOAuth2Authentication(client, tokenRequest);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                (UsernamePasswordAuthenticationToken) oAuth2Authentication.getUserAuthentication();
        if (userEntityRepository.isTfaEnabled(usernamePasswordAuthenticationToken.getName())) {
            String code = authorizationCodeServices.createAuthorizationCode(oAuth2Authentication);
            log.info("Code [{}]", code);
            throw new TfaRequiredException();
        }
        return getTokenServices().createAccessToken(oAuth2Authentication);
    }
}

package com.ecaservice.oauth.config.granter;

import com.ecaservice.oauth.service.tfa.TfaCodeService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;

import java.util.HashMap;
import java.util.Map;

/**
 * Two factor authentication token granter.
 *
 * @author Roman Batygin
 */
public class TfaCodeTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "tfa_code";
    private static final String TFA_CODE_PARAM = "tfa_code";
    private static final String TOKEN_PARAM = "token";

    private final TfaCodeService tfaCodeService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param endpoints                 - authorization server endpoints configurer bean
     * @param tfaCodeService - authorization code services
     */
    public TfaCodeTokenGranter(AuthorizationServerEndpointsConfigurer endpoints,
                               TfaCodeService tfaCodeService) {
        this(endpoints, tfaCodeService, GRANT_TYPE);
    }

    protected TfaCodeTokenGranter(AuthorizationServerEndpointsConfigurer endpoints,
                                  TfaCodeService tfaCodeService,
                                  String grantType) {
        super(endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory(),
                grantType);
        this.tfaCodeService = tfaCodeService;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = tokenRequest.getRequestParameters();
        String token = parameters.get(TOKEN_PARAM);
        if (token == null) {
            throw new InvalidRequestException("Token must be supplied.");
        }
        String tfaCode = parameters.get(TFA_CODE_PARAM);
        if (tfaCode == null) {
            throw new InvalidRequestException("Tfa code must be supplied.");
        }
        OAuth2Authentication storedAuth = tfaCodeService.consumeAuthorizationCode(token, tfaCode);
        return createNewOauth2Authentication(tokenRequest, storedAuth);
    }

    private OAuth2Authentication createNewOauth2Authentication(TokenRequest tokenRequest,
                                                               OAuth2Authentication storedAuth) {
        OAuth2Request pendingOAuth2Request = storedAuth.getOAuth2Request();
        String pendingClientId = pendingOAuth2Request.getClientId();
        String clientId = tokenRequest.getClientId();
        if (clientId != null && !clientId.equals(pendingClientId)) {
            // just a sanity check.
            throw new InvalidClientException("Client ID mismatch");
        }
        Map<String, String> combinedParameters = new HashMap<>(pendingOAuth2Request.getRequestParameters());
        // Combine the parameters adding the new ones last so they override if there are any clashes
        combinedParameters.putAll(tokenRequest.getRequestParameters());
        // Make a new stored request with the combined parameters
        OAuth2Request finalStoredOAuth2Request = pendingOAuth2Request.createOAuth2Request(combinedParameters);
        Authentication userAuth = storedAuth.getUserAuthentication();
        return new OAuth2Authentication(finalStoredOAuth2Request, userAuth);
    }
}

package com.ecaservice.oauth.config.granter;

import com.ecaservice.oauth.config.TfaConfig;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.service.mail.NotificationService;
import com.ecaservice.oauth.service.tfa.TfaCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Token granter configurer.
 *
 * @author Roman Batygin
 */
@Component
@RequiredArgsConstructor
public class TokenGranterConfigurer {

    private final TfaConfig tfaConfig;
    private final NotificationService notificationService;
    private final AuthenticationManager authenticationManager;
    private final TfaCodeService tfaCodeService;
    private final UserEntityRepository userEntityRepository;

    /**
     * Extends token granters.
     *
     * @param endpoints - authorization endpoints configurer bean.
     */
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        List<TokenGranter> tokenGranters = newArrayList();
        tokenGranters.add(endpoints.getTokenGranter());
        tokenGranters.add(new TfaResourceOwnerPasswordTokenGranter(authenticationManager, endpoints, tfaConfig,
                notificationService, tfaCodeService, userEntityRepository));
        tokenGranters.add(new TfaCodeTokenGranter(endpoints, tfaCodeService));
        CompositeTokenGranter compositeTokenGranter = new CompositeTokenGranter(tokenGranters);
        endpoints.tokenGranter(compositeTokenGranter);
    }
}

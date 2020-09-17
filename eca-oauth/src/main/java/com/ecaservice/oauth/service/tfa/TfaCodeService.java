package com.ecaservice.oauth.service.tfa;

import com.ecaservice.oauth.config.TfaConfig;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * TFA code service.
 *
 * @author Roman Batygin
 */
@Service
@RequiredArgsConstructor
public class TfaCodeService implements AuthorizationCodeServices {

    private final TfaConfig tfaConfig;

    private Cache<String, OAuth2Authentication> codesCache;

    private Map<String, Object> monitorsMap = new ConcurrentHashMap<>();

    private RandomValueStringGenerator generator = new RandomValueStringGenerator();

    /**
     * Initialize cache.
     */
    @PostConstruct
    public void initialize() {
        this.codesCache = CacheBuilder.newBuilder()
                .expireAfterWrite(tfaConfig.getCodeValiditySeconds(), TimeUnit.SECONDS)
                .build();
        this.generator.setLength(tfaConfig.getCodeLength());
    }

    @Override
    public String createAuthorizationCode(OAuth2Authentication authentication) {
        String code = generator.generate();
        String user = authentication.getName();
        monitorsMap.putIfAbsent(user, new Object());
        synchronized (monitorsMap.get(user)) {
            //Invalidate previous code
            invalidatePreviousCode(user);
            codesCache.put(code, authentication);
        }
        monitorsMap.remove(user);
        return code;
    }

    @Override
    public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {
        OAuth2Authentication authentication = codesCache.getIfPresent(code);
        if (authentication == null) {
            throw new InvalidGrantException(String.format("Invalid authorization code: %s", code));
        }
        codesCache.invalidate(code);
        return authentication;
    }

    private void invalidatePreviousCode(String user) {
        codesCache.asMap().entrySet()
                .stream()
                .filter(entry -> entry.getValue().getName().equals(user))
                .map(Map.Entry::getKey)
                .findFirst()
                .ifPresent(oldCode -> codesCache.invalidate(oldCode));
    }
}

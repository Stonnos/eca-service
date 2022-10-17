package com.ecaservice.web.push.service;

import com.ecaservice.common.web.crypto.EncryptorBase64AdapterService;
import com.ecaservice.web.dto.model.PushTokenDto;
import com.ecaservice.web.push.config.AppProperties;
import com.ecaservice.web.push.entity.PushTokenEntity;
import com.ecaservice.web.push.repository.PushTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service to manage with push tokens.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PushTokenService {

    private final AppProperties appProperties;
    private final EncryptorBase64AdapterService encryptorBase64AdapterService;
    private final PushTokenRepository pushTokenRepository;

    /**
     * Obtains push token for specified user.
     *
     * @param user - user
     * @return push token dto
     */
    public PushTokenDto obtainToken(String user) {
        log.info("Starting to obtain push token for user [{}]", user);
        var pushTokenEntity = pushTokenRepository.findByUser(user);
        if (pushTokenEntity == null) {
            pushTokenEntity = new PushTokenEntity();
            return createNewToken(pushTokenEntity, user);
        } else if (pushTokenEntity.isExpired()) {
            log.warn("Push token has been expired for user [{}]", user);
            return createNewToken(pushTokenEntity, user);
        } else {
            String tokenId = encryptorBase64AdapterService.decrypt(pushTokenEntity.getTokenId());
            log.info("Valid push token has been obtained for user [{}]", user);
            return PushTokenDto.builder()
                    .tokenId(tokenId)
                    .build();
        }
    }

    private PushTokenDto createNewToken(PushTokenEntity pushTokenEntity, String user) {
        String tokenId = UUID.randomUUID().toString();
        pushTokenEntity.setTokenId(tokenId);
        pushTokenEntity.setUser(user);
        pushTokenEntity.setExpireAt(LocalDateTime.now().plusMinutes(appProperties.getPushTokenValidityMinutes()));
        pushTokenRepository.save(pushTokenEntity);
        log.info("New push token has been created for user [{}]", user);
        return PushTokenDto.builder()
                .tokenId(pushTokenEntity.getTokenId())
                .build();
    }
}

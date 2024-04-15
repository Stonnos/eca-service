package com.ecaservice.oauth.service;

import com.ecaservice.oauth.entity.UserProfileOptionsDataEventEntity;
import com.ecaservice.oauth.entity.UserProfileOptionsEntity;
import com.ecaservice.oauth.event.model.UserProfileOptionsDataEvent;
import com.ecaservice.oauth.mapping.UserProfileOptionsMapper;
import com.ecaservice.oauth.repository.UserProfileOptionsDataEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.ecaservice.common.web.util.JsonUtils.toJson;

/**
 * User profile options data event service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileOptionsDataEventService {

    private final UserProfileOptionsMapper userProfileOptionsMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserProfileOptionsDataEventRepository userProfileOptionsDataEventRepository;

    /**
     * Save user profile options event to sent using transaction outbox mechanism.
     * NOTE: Method must be called in transaction for the transaction outbox mechanism to work.
     *
     * @param userProfileOptionsEntity - user profile options entity
     */
    public void saveEvent(UserProfileOptionsEntity userProfileOptionsEntity) {
        var userProfileOptionsDto = userProfileOptionsMapper.mapToDto(userProfileOptionsEntity);
        String requestId = UUID.randomUUID().toString();
        log.info("Starting to save user [{}] profile options data event [{}]: [{}]",
                userProfileOptionsDto.getUser(), requestId, userProfileOptionsDto);
        var userProfileOptionsDataEventEntity = new UserProfileOptionsDataEventEntity();
        userProfileOptionsDataEventEntity.setRequestId(requestId);
        userProfileOptionsDataEventEntity.setMessageBody(toJson(userProfileOptionsDto));
        userProfileOptionsDataEventEntity.setCreated(LocalDateTime.now());
        userProfileOptionsDataEventRepository.save(userProfileOptionsDataEventEntity);
        // Publish saved event to transaction committed listener
        applicationEventPublisher.publishEvent(new UserProfileOptionsDataEvent(this));
        log.info("User [{}] profile options data event [{}] has saved [{}]", userProfileOptionsDto.getUser(),
                requestId, userProfileOptionsDto);
    }
}

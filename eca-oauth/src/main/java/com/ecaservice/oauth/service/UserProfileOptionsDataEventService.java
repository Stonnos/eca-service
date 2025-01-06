package com.ecaservice.oauth.service;

import com.ecaservice.core.transactional.outbox.model.OutboxMessage;
import com.ecaservice.core.transactional.outbox.service.OutboxMessageService;
import com.ecaservice.oauth.entity.UserProfileOptionsEntity;
import com.ecaservice.oauth.mapping.UserProfileOptionsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static com.ecaservice.oauth.config.outbox.OutboxMessageCode.USER_PROFILE_OPTIONS_DATA;

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
    private final OutboxMessageService outboxMessageService;

    /**
     * Save user profile options event to sent using transaction outbox mechanism.
     * NOTE: Method must be called in transaction for the transaction outbox mechanism to work.
     *
     * @param userProfileOptionsEntity - user profile options entity
     */
    public void saveEvent(UserProfileOptionsEntity userProfileOptionsEntity) {
        var userProfileOptionsDto = userProfileOptionsMapper.mapToDto(userProfileOptionsEntity);
        log.info("Starting to save user [{}] profile options data event: [{}]",
                userProfileOptionsDto.getUser(), userProfileOptionsDto);
        OutboxMessage outboxMessage = new OutboxMessage(USER_PROFILE_OPTIONS_DATA, userProfileOptionsDto);
        outboxMessageService.saveOutboxMessage(Collections.singletonList(outboxMessage));
        log.info("User [{}] profile options data event has saved [{}]", userProfileOptionsDto.getUser(),
                userProfileOptionsDto);
    }
}

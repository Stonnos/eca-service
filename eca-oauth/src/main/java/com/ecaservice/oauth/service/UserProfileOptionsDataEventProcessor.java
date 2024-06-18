package com.ecaservice.oauth.service;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.oauth.entity.UserProfileOptionsDataEventEntity;
import com.ecaservice.oauth.repository.UserProfileOptionsDataEventRepository;
import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static com.ecaservice.common.web.util.JsonUtils.fromJson;

/**
 * User profile options data event processor.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileOptionsDataEventProcessor {

    private final UserProfileOptionsDataEventSender userProfileOptionsDataEventSender;
    private final UserProfileOptionsDataEventRepository userProfileOptionsDataEventRepository;

    /**
     * Sent user profile options data events to mq.
     */
    @Locked(lockName = "sendUserProfileOptionsDataEvents", waitForLock = false)
    public void sendEvents() {
        log.debug("Starting to process user profile options data events");
        var userProfileOptionsDataEvents = userProfileOptionsDataEventRepository.findAll();
        if (!CollectionUtils.isEmpty(userProfileOptionsDataEvents)) {
            log.info("Found [{}] user profile options data events to sent", userProfileOptionsDataEvents.size());
            userProfileOptionsDataEvents.forEach(this::sendDataEvent);
        }
        log.debug("User profile options data events has been processes");
    }

    private void sendDataEvent(UserProfileOptionsDataEventEntity userProfileOptionsDataEventEntity) {
        try {
            log.info("Starting to sent user profile options data event [{}]",
                    userProfileOptionsDataEventEntity.getRequestId());
            var userProfileOptionsDto =
                    fromJson(userProfileOptionsDataEventEntity.getMessageBody(), UserProfileOptionsDto.class);
            userProfileOptionsDataEventSender.send(userProfileOptionsDto);
            userProfileOptionsDataEventRepository.deleteEvent(userProfileOptionsDataEventEntity.getId());
            log.info("User profile options data event [{}] has been sent",
                    userProfileOptionsDataEventEntity.getRequestId());
        } catch (Exception ex) {
            log.error("Error while send user profile options data event [{}]. Error details: {}",
                    userProfileOptionsDataEventEntity.getRequestId(), ex.getMessage());
        }
    }
}

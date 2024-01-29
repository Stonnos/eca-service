package com.ecaservice.oauth.event.listener;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.oauth.entity.UserProfileOptionsEntity;
import com.ecaservice.oauth.event.model.UserProfileOptionsDataEvent;
import com.ecaservice.oauth.mapping.UserProfileOptionsMapper;
import com.ecaservice.oauth.repository.UserProfileOptionsRepository;
import com.ecaservice.oauth.service.UserProfileOptionsDataEventSender;
import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * User profile options data event listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileOptionsDataEventListener {

    private final UserProfileOptionsDataEventSender userProfileOptionsDataEventSender;
    private final UserProfileOptionsMapper userProfileOptionsMapper;
    private final UserProfileOptionsRepository userProfileOptionsRepository;

    /**
     * Handles user profile options data event.
     *
     * @param userProfileOptionsDataEvent - user profile options data event
     */
    @EventListener
    public void handleEvent(UserProfileOptionsDataEvent userProfileOptionsDataEvent) {
        log.info("Starting to handle user [{}] profile options data event from [{}]",
                userProfileOptionsDataEvent.getUserEntity().getLogin(), userProfileOptionsDataEvent.getSource());
        var userProfileOptionsEntity =
                userProfileOptionsRepository.findByUserEntity(userProfileOptionsDataEvent.getUserEntity());
        if (userProfileOptionsEntity == null) {
            throw new EntityNotFoundException(UserProfileOptionsEntity.class,
                    userProfileOptionsDataEvent.getUserEntity().getLogin());
        }
        UserProfileOptionsDto userProfileOptionsDto = userProfileOptionsMapper.mapToDto(userProfileOptionsEntity);
        sendDataEvent(userProfileOptionsDto);
        log.info("User [{}] profile options data event from [{}] has been processed",
                userProfileOptionsDataEvent.getUserEntity().getLogin(), userProfileOptionsDataEvent.getSource());
    }

    private void sendDataEvent(UserProfileOptionsDto userProfileOptionsDto) {
        try {
            userProfileOptionsDataEventSender.send(userProfileOptionsDto);
        } catch (Exception ex) {
            log.error("Error while send user [{}] profile options data event [{}]. Error details: {}",
                    userProfileOptionsDto.getUser(), userProfileOptionsDto, ex.getMessage());
        }
    }
}

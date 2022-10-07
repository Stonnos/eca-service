package com.ecaservice.web.push.service;

import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.SimplePageRequestDto;
import com.ecaservice.web.dto.model.UserNotificationDto;
import com.ecaservice.web.push.config.AppProperties;
import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import com.ecaservice.web.push.entity.MessageStatus;
import com.ecaservice.web.push.entity.NotificationEntity;
import com.ecaservice.web.push.mapping.NotificationMapper;
import com.ecaservice.web.push.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User notification service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserNotificationService {

    private final AppProperties appProperties;
    private final UserService userService;
    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;

    /**
     * Gets current user notifications next page for specified page request and last 7 days.
     *
     * @param pageRequestDto - page request
     * @return users entities page
     */
    public PageDto<UserNotificationDto> getNextPage(SimplePageRequestDto pageRequestDto) {
        String currentUser = userService.getCurrentUser();
        log.info("Gets user [{}] notifications next page: {}", currentUser, pageRequestDto);
        int pageSize = Integer.min(pageRequestDto.getSize(), appProperties.getMaxPageSize());
        LocalDateTime date = LocalDateTime.now().minusDays(appProperties.getNotificationLifeTimeDays());
        var pageRequest = PageRequest.of(pageRequestDto.getPage(), pageSize);
        var notificationsPage =
                notificationRepository.findByReceiverAndCreatedIsAfter(currentUser, date, pageRequest);
        log.info("User [{}] notifications page [{} of {}] with size [{}] has been fetched for page request [{}]",
                currentUser, notificationsPage.getNumber(), notificationsPage.getTotalPages(),
                notificationsPage.getNumberOfElements(), pageRequestDto);
        var notificationDtoList = notificationMapper.map(notificationsPage.getContent());
        return PageDto.of(notificationDtoList, pageRequestDto.getPage(), notificationsPage.getTotalElements());
    }

    /**
     * Saves user push notification request.
     *
     * @param userPushNotificationRequest - user push notification request
     */
    public void save(UserPushNotificationRequest userPushNotificationRequest) {
        log.info("Starting to save user push notification request [{}] from initiator [{}] to receivers {}",
                userPushNotificationRequest.getRequestId(), userPushNotificationRequest.getInitiator(),
                userPushNotificationRequest.getReceivers());
        var notifications = createNotifications(userPushNotificationRequest);
        notificationRepository.saveAll(notifications);
        log.info("[{}] notifications has been saved for push notification request [{}]",
                userPushNotificationRequest.getRequestId(), notifications.size());
    }

    private List<NotificationEntity> createNotifications(UserPushNotificationRequest userPushNotificationRequest) {
        return userPushNotificationRequest.getReceivers().stream()
                .map(receiver -> {
                    var notification = notificationMapper.map(userPushNotificationRequest);
                    notification.setReceiver(receiver);
                    notification.setMessageStatus(MessageStatus.NOT_READ);
                    notification.setCreated(LocalDateTime.now());
                    return notification;
                })
                .collect(Collectors.toList());
    }
}

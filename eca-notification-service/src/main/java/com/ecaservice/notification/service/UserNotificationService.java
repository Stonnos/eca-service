package com.ecaservice.notification.service;

import com.ecaservice.notification.config.WebPushProperties;
import com.ecaservice.notification.entity.MessageStatus;
import com.ecaservice.notification.entity.UserNotificationEntity;
import com.ecaservice.notification.exception.InvalidNotificationsIdsException;
import com.ecaservice.notification.mapping.NotificationMapper;
import com.ecaservice.notification.repository.UserNotificationRepository;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.ReadNotificationsDto;
import com.ecaservice.web.dto.model.SimplePageRequestDto;
import com.ecaservice.web.dto.model.UserNotificationDto;
import com.ecaservice.web.dto.model.UserNotificationStatisticsDto;
import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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

    private final WebPushProperties webPushProperties;
    private final UserService userService;
    private final NotificationMapper notificationMapper;
    private final UserNotificationRepository userNotificationRepository;

    /**
     * Saves user push notification request.
     *
     * @param userPushNotificationRequest - user push notification request
     */
    public void save(UserPushNotificationRequest userPushNotificationRequest) {
        log.info("Starting to save user push notification request [{}] type [{}] from initiator [{}] to receivers {}",
                userPushNotificationRequest.getRequestId(), userPushNotificationRequest.getMessageType(),
                userPushNotificationRequest.getInitiator(), userPushNotificationRequest.getReceivers());
        var notifications = createNotifications(userPushNotificationRequest);
        userNotificationRepository.saveAll(notifications);
        log.info("[{}] notifications has been saved for push notification request [{}]", notifications.size(),
                userPushNotificationRequest.getRequestId());
    }

    /**
     * Gets current user notifications next page for specified page request and last 7 days.
     *
     * @param pageRequestDto - page request
     * @return users entities page
     */
    public PageDto<UserNotificationDto> getNextPage(SimplePageRequestDto pageRequestDto) {
        String currentUser = userService.getCurrentUser();
        log.info("Gets user [{}] notifications next page: {}", currentUser, pageRequestDto);
        LocalDateTime date = LocalDateTime.now().minusDays(webPushProperties.getNotificationLifeTimeDays());
        var pageRequest = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize());
        var notificationsPage =
                userNotificationRepository.findByReceiverAndCreatedIsAfterOrderByCreatedDesc(currentUser, date,
                        pageRequest);
        log.info("User [{}] notifications page [{} of {}] with size [{}] has been fetched for page request [{}]",
                currentUser, notificationsPage.getNumber(), notificationsPage.getTotalPages(),
                notificationsPage.getNumberOfElements(), pageRequestDto);
        var notificationDtoList = notificationMapper.map(notificationsPage.getContent());
        return PageDto.of(notificationDtoList, pageRequestDto.getPage(), notificationsPage.getTotalElements());
    }

    /**
     * Gets notifications statistics for current user.
     *
     * @return notifications statistics
     */
    public UserNotificationStatisticsDto getNotificationStatistics() {
        String currentUser = userService.getCurrentUser();
        log.info("Gets not read notification count for user [{}]", currentUser);
        LocalDateTime date = LocalDateTime.now().minusDays(webPushProperties.getNotificationLifeTimeDays());
        long notReadCount = userNotificationRepository.getNotReadNotificationsCount(currentUser, date);
        log.info("[{}] not read notification count has been calculated for user [{}]", notReadCount, currentUser);
        return UserNotificationStatisticsDto.builder()
                .notReadCount(notReadCount)
                .build();
    }

    /**
     * Reads notifications.
     *
     * @param readNotificationsDto - read notifications dto
     */
    @Transactional
    public void readNotifications(ReadNotificationsDto readNotificationsDto) {
        String currentUser = userService.getCurrentUser();
        var ids = readNotificationsDto.getIds();
        log.info("Starting to read user [{}] notifications size [{}], ids {}", currentUser, ids.size(), ids);
        if (!CollectionUtils.isEmpty(ids)) {
            var fetchedIds = userNotificationRepository.getNotifications(ids, currentUser);
            var invalidIds = ids.stream()
                    .filter(id -> !fetchedIds.contains(id))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(invalidIds)) {
                throw new InvalidNotificationsIdsException(invalidIds);
            }
            long readCount = userNotificationRepository.readNotifications(currentUser, ids);
            log.info("[{}] notifications has been read for user [{}]", readCount, currentUser);
        } else {
            log.info("Starting to read all not read notifications for user [{}]", currentUser);
            LocalDateTime date = LocalDateTime.now().minusDays(webPushProperties.getNotificationLifeTimeDays());
            long readCount = userNotificationRepository.readAllNotifications(currentUser, date);
            log.info("[{}] notifications has been read for user [{}]", readCount, currentUser);
        }
    }

    private List<UserNotificationEntity> createNotifications(UserPushNotificationRequest userPushNotificationRequest) {
        return userPushNotificationRequest.getReceivers()
                .stream()
                .map(receiver -> {
                    var notification = notificationMapper.map(userPushNotificationRequest);
                    notification.setReceiver(receiver);
                    notification.setMessageStatus(MessageStatus.NOT_READ);
                    return notification;
                })
                .collect(Collectors.toList());
    }
}

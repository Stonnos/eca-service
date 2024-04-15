package com.ecaservice.oauth.service;

import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.mapping.UserProfileOptionsMapperImpl;
import com.ecaservice.oauth.repository.UserProfileOptionsDataEventRepository;
import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.stream.IntStream;

import static com.ecaservice.oauth.TestHelperUtils.createUserProfileOptionsDataEventEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for checking {@link UserProfileOptionsDataEventService} functionality.
 *
 * @author Roman Batygin
 */
@Import({UserProfileOptionsDataEventProcessor.class, UserProfileOptionsMapperImpl.class})
class UserProfileOptionsDataEventProcessorTest extends AbstractJpaTest {

    private static final int EVENTS_NUM = 5;

    @MockBean
    private UserProfileOptionsDataEventSender userProfileOptionsDataEventSender;

    @Autowired
    private UserProfileOptionsDataEventRepository userProfileOptionsDataEventRepository;

    @Autowired
    private UserProfileOptionsDataEventProcessor userProfileOptionsDataEventProcessor;

    @Override
    public void init() {
        IntStream.range(0, EVENTS_NUM).forEach(
                i -> userProfileOptionsDataEventRepository.save(createUserProfileOptionsDataEventEntity())
        );
    }

    @Override
    public void deleteAll() {
        userProfileOptionsDataEventRepository.deleteAll();
    }

    @Test
    void testSendEvents() {
        userProfileOptionsDataEventProcessor.sendEvents();
        verify(userProfileOptionsDataEventSender, times(EVENTS_NUM)).send(any(UserProfileOptionsDto.class));
        assertThat(userProfileOptionsDataEventRepository.count()).isZero();
    }
}

package com.ecaservice.oauth.service;

import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.entity.UserProfileOptionsEntity;
import com.ecaservice.oauth.mapping.UserProfileOptionsMapperImpl;
import com.ecaservice.oauth.repository.UserProfileOptionsDataEventRepository;
import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.ecaservice.common.web.util.JsonUtils.fromJson;
import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static com.ecaservice.oauth.TestHelperUtils.createUserProfileOptionsEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link UserProfileOptionsDataEventService} functionality.
 *
 * @author Roman Batygin
 */
@Import({UserProfileOptionsDataEventService.class, UserProfileOptionsMapperImpl.class})
class UserProfileOptionsDataEventServiceTest extends AbstractJpaTest {

    @Autowired
    private UserProfileOptionsDataEventRepository userProfileOptionsDataEventRepository;

    @Autowired
    private UserProfileOptionsDataEventService userProfileOptionsDataEventService;

    @Override
    public void deleteAll() {
        userProfileOptionsDataEventRepository.deleteAll();
    }

    @Test
    void testSaveUserProfileOptionsDataEvent() {
        UserProfileOptionsEntity userProfileOptionsEntity = createUserProfileOptionsEntity(createUserEntity());
        userProfileOptionsDataEventService.saveEvent(userProfileOptionsEntity);
        var events = userProfileOptionsDataEventRepository.findAll();
        assertThat(events).hasSize(1);
        var event = events.iterator().next();
        assertThat(events).isNotNull();
        assertThat(event.getRequestId()).isNotNull();
        assertThat(event.getMessageBody()).isNotNull();
        UserProfileOptionsDto userProfileOptionsDto = fromJson(event.getMessageBody(), UserProfileOptionsDto.class);
        assertThat(userProfileOptionsDto).isNotNull();
        assertThat(userProfileOptionsDto.getUser()).isEqualTo(userProfileOptionsEntity.getUserEntity().getLogin());
    }
}

package com.ecaservice.user.profile.options.cache.client.service;

import com.ecaservice.user.profile.options.cache.client.AbstractJpaTest;
import com.ecaservice.user.profile.options.cache.client.repository.UserProfileOptionsDataRepository;
import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.ecaservice.user.profile.options.cache.client.TestHelperUtils.loadUserProfileOptionsDto;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests fot {@link UserProfileOptionsDataService} class.
 *
 * @author Roman Batygin
 */
@Import(UserProfileOptionsDataService.class)
class UserProfileOptionsDataServiceTest extends AbstractJpaTest {

    @Autowired
    private UserProfileOptionsDataRepository userProfileOptionsDataRepository;

    @Autowired
    private UserProfileOptionsDataService userProfileOptionsDataService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserProfileOptionsDto userProfileOptionsDto;

    @Override
    public void init() {
        userProfileOptionsDto = loadUserProfileOptionsDto();
    }

    @Override
    public void deleteAll() {
        userProfileOptionsDataRepository.deleteAll();
    }

    @Test
    void testCreateUserProfileOptions() throws JsonProcessingException {
        userProfileOptionsDataService.createOrUpdateUserProfileOptions(userProfileOptionsDto);
        verifyUserProfileOptions();
    }

    @Test
    void testUpdateUserProfileOptions() throws JsonProcessingException {
        userProfileOptionsDataService.createOrUpdateUserProfileOptions(userProfileOptionsDto);
        userProfileOptionsDto.setEmailEnabled(false);
        userProfileOptionsDto.setVersion(userProfileOptionsDto.getVersion() + 1);
        userProfileOptionsDataService.createOrUpdateUserProfileOptions(userProfileOptionsDto);
        verifyUserProfileOptions();
    }

    @Test
    void testUpdateUserProfileOptionsWithInvalidVersion() {
        userProfileOptionsDataService.createOrUpdateUserProfileOptions(userProfileOptionsDto);
        userProfileOptionsDto.setEmailEnabled(false);
        userProfileOptionsDto.setVersion(userProfileOptionsDto.getVersion() - 1);
        assertThrows(IllegalStateException.class,
                () -> userProfileOptionsDataService.createOrUpdateUserProfileOptions(userProfileOptionsDto));
    }

    private void verifyUserProfileOptions() throws JsonProcessingException {
        var userProfileOptionsData = userProfileOptionsDataRepository.findByUser(userProfileOptionsDto.getUser());
        assertThat(userProfileOptionsData).isNotNull();
        assertThat(userProfileOptionsData.getUser()).isEqualTo(userProfileOptionsDto.getUser());
        assertThat(userProfileOptionsData.getVersion()).isEqualTo(userProfileOptionsDto.getVersion());
        assertThat(userProfileOptionsData.getCreated()).isNotNull();
        assertThat(userProfileOptionsData.getUpdated()).isNotNull();
        assertThat(userProfileOptionsData.getOptionsJson()).isEqualTo(
                objectMapper.writeValueAsString(userProfileOptionsDto));
    }
}

package com.ecaservice.user.profile.options.cache.client.service;

import com.ecaservice.user.profile.options.cache.client.AbstractJpaTest;
import com.ecaservice.user.profile.options.cache.client.repository.UserProfileOptionsDataRepository;
import com.ecaservice.user.profile.options.client.service.UserProfileOptionsClient;
import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static com.ecaservice.user.profile.options.cache.client.TestHelperUtils.createEserProfileOptionsData;
import static com.ecaservice.user.profile.options.cache.client.TestHelperUtils.loadUserProfileOptionsDto;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests fot {@link UserProfileOptionsCacheProvider} class.
 *
 * @author Roman Batygin
 */
@Import({UserProfileOptionsCacheProvider.class, UserProfileOptionsDataService.class})
class UserProfileOptionsCacheProviderTest extends AbstractJpaTest {

    private static final String USER = "admin";

    @MockBean
    private UserProfileOptionsClient userProfileOptionsClient;

    @Autowired
    private UserProfileOptionsDataRepository userProfileOptionsDataRepository;

    @Autowired
    private UserProfileOptionsCacheProvider userProfileOptionsCacheProvider;

    @Override
    public void deleteAll() {
        userProfileOptionsDataRepository.deleteAll();
    }

    @Test
    void testGetNotFoundUserProfileOptions() {
        when(userProfileOptionsClient.getUserProfileOptions(USER))
                .thenReturn(loadUserProfileOptionsDto());
        var userProfileOptions =
                userProfileOptionsCacheProvider.getUserProfileOptions(USER);
        verifyUserProfileOptions(userProfileOptions);
    }

    @Test
    void testGetUserProfileOptionsFromCache() {
        var userProfileOptionsData = createEserProfileOptionsData();
        userProfileOptionsDataRepository.save(userProfileOptionsData);
        var userProfileOptions =
                userProfileOptionsCacheProvider.getUserProfileOptions(USER);
        verifyUserProfileOptions(userProfileOptions);
    }

    private void verifyUserProfileOptions(UserProfileOptionsDto userProfileOptionsDto) {
        assertThat(userProfileOptionsDto).isNotNull();
        var userProfileOptionsData = userProfileOptionsDataRepository.findByUser(userProfileOptionsDto.getUser());
        assertThat(userProfileOptionsData).isNotNull();
        assertThat(userProfileOptionsDto.getUser()).isEqualTo(userProfileOptionsData.getUser());
        assertThat(userProfileOptionsDto.getVersion()).isEqualTo(userProfileOptionsData.getVersion());
    }
}

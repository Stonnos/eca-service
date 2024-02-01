package com.ecaservice.user.profile.options.cache.client;

import com.ecaservice.user.profile.options.cache.client.entity.UserProfileOptionsDataEntity;
import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String USER_PROFILE_OPTIONS_JSON = "user_profile_options.json";

    /**
     * Loads user profile options dto.
     *
     * @return user profile options dto
     */
    public static UserProfileOptionsDto loadUserProfileOptionsDto() {
        try {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            @Cleanup var inputStream = classLoader.getResourceAsStream(USER_PROFILE_OPTIONS_JSON);
            return OBJECT_MAPPER.readValue(inputStream, UserProfileOptionsDto.class);
        } catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }

    /**
     * Creates user profile options data entity.
     *
     * @return user profile options data entity
     */
    @SneakyThrows
    public static UserProfileOptionsDataEntity createEserProfileOptionsData() {
        var userProfileOptionsDto = loadUserProfileOptionsDto();
        UserProfileOptionsDataEntity userProfileOptionsData = new UserProfileOptionsDataEntity();
        userProfileOptionsData.setUser(userProfileOptionsDto.getUser());
        userProfileOptionsData.setVersion(userProfileOptionsDto.getVersion());
        userProfileOptionsData.setOptionsJson(OBJECT_MAPPER.writeValueAsString(loadUserProfileOptionsDto()));
        userProfileOptionsData.setCreated(LocalDateTime.now());
        userProfileOptionsData.setUpdated(LocalDateTime.now());
        return userProfileOptionsData;
    }
}

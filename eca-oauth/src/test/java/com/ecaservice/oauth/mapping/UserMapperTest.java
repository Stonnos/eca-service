package com.ecaservice.oauth.mapping;

import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.dto.UpdateUserInfoDto;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.user.model.UserDetailsImpl;
import com.ecaservice.web.dto.model.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.oauth.TestHelperUtils.createUpdateUserInfoDto;
import static com.ecaservice.oauth.TestHelperUtils.createUserDto;
import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link UserMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({RoleMapperImpl.class, UserMapperImpl.class})
class UserMapperTest {

    @Inject
    private UserMapper userMapper;

    @Test
    void testMapUserEntityToUserDto() {
        UserEntity userEntity = createUserEntity();
        UserDto userDto = userMapper.map(userEntity);
        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(userEntity.getId());
        assertThat(userDto.getLogin()).isEqualTo(userEntity.getLogin());
        assertThat(userDto.getEmail()).isEqualTo(userEntity.getEmail());
        assertThat(userDto.getFirstName()).isEqualTo(userEntity.getFirstName());
        assertThat(userDto.getLastName()).isEqualTo(userEntity.getLastName());
        assertThat(userDto.getMiddleName()).isEqualTo(userEntity.getMiddleName());
        assertThat(userDto.getRoles()).isNotEmpty();
        assertThat(userDto.getRoles()).hasSameSizeAs(userEntity.getRoles());
        assertThat(userDto.getCreationDate()).isEqualTo(userEntity.getCreationDate());
        assertThat(userDto.isTfaEnabled()).isEqualTo(userEntity.isTfaEnabled());
    }

    @Test
    void testMapCreateUserDtoToUserEntity() {
        CreateUserDto createUserDto = createUserDto();
        UserEntity userEntity = userMapper.map(createUserDto);
        assertThat(userEntity).isNotNull();
        assertThat(userEntity.getLogin()).isEqualTo(createUserDto.getLogin());
        assertThat(userEntity.getEmail()).isEqualTo(createUserDto.getEmail());
        assertThat(userEntity.getFirstName()).isEqualTo(createUserDto.getFirstName());
        assertThat(userEntity.getLastName()).isEqualTo(createUserDto.getLastName());
        assertThat(userEntity.getMiddleName()).isEqualTo(createUserDto.getMiddleName());
    }

    @Test
    void testMapUserEntityToUserDetails() {
        UserEntity userEntity = createUserEntity();
        UserDetailsImpl userDetails = userMapper.mapDetails(userEntity);
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getId()).isEqualTo(userEntity.getId());
        assertThat(userDetails.getUsername()).isEqualTo(userEntity.getLogin());
        assertThat(userDetails.getAuthorities()).isNotEmpty();
        assertThat(userDetails.getAuthorities()).hasSameSizeAs(userEntity.getRoles());
    }

    @Test
    void testUpdateUserInfo() {
        UpdateUserInfoDto updateUserInfoDto = createUpdateUserInfoDto();
        UserEntity userEntity = createUserEntity();
        userMapper.update(updateUserInfoDto, userEntity);
        assertThat(userEntity.getFirstName()).isEqualTo(updateUserInfoDto.getFirstName());
        assertThat(userEntity.getLastName()).isEqualTo(updateUserInfoDto.getLastName());
        assertThat(userEntity.getMiddleName()).isEqualTo(updateUserInfoDto.getMiddleName());
    }
}

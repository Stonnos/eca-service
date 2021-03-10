package com.ecaservice.oauth.mapping;

import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.user.model.UserDetailsImpl;
import com.ecaservice.web.dto.model.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Optional;

/**
 * Users mapper.
 *
 * @author Roman Batygin
 */
@Mapper(uses = RoleMapper.class)
public interface UserMapper {

    /**
     * Maps user entity to its dto model.
     *
     * @param userEntity - user entity
     * @return user dto
     */
    UserDto map(UserEntity userEntity);

    /**
     * Maps create user dto to entity model
     *
     * @param createUserDto - create user dto
     * @return user entity
     */
    @Mapping(source = "login", target = "login", qualifiedByName = "trim")
    @Mapping(source = "email", target = "email", qualifiedByName = "trim")
    @Mapping(source = "firstName", target = "firstName", qualifiedByName = "trim")
    UserEntity map(CreateUserDto createUserDto);

    /**
     * Maps user entity to user details.
     *
     * @param userEntity - user entity
     * @return user details
     */
    @Mapping(source = "login", target = "userName")
    @Mapping(source = "roles", target = "authorities")
    UserDetailsImpl mapDetails(UserEntity userEntity);

    /**
     * Maps user entities to its dto model list.
     *
     * @param userEntityList - users entities
     * @return users dto list
     */
    List<UserDto> map(List<UserEntity> userEntityList);

    @Named("trim")
    default String trim(String value) {
        return Optional.ofNullable(value).map(String::trim).orElse(null);
    }
}

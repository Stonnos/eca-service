package com.ecaservice.oauth.mapping;

import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.user.model.UserDetailsImpl;
import com.ecaservice.web.dto.model.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Users mapper.
 *
 * @author Roman Batygin
 */
@Mapper(uses = RoleMapper.class)
public interface UserMapper {

    /**
     * Maps user details to user dto.
     *
     * @param userDetails - user details
     * @return user dto
     */
    @Mapping(source = "username", target = "login")
    @Mapping(source = "authorities", target = "roles")
    UserDto map(UserDetailsImpl userDetails);

    /**
     * Maps user entity to its dto model.
     *
     * @param userEntity - user entity
     * @return user dto
     */
    UserDto map(UserEntity userEntity);

    /**
     * Maps user entities to its dto model list.
     *
     * @param userEntityList - users entities
     * @return users dto list
     */
    List<UserDto> map(List<UserEntity> userEntityList);
}

package com.ecaservice.oauth.mapping;

import com.ecaservice.oauth.entity.RoleEntity;
import com.ecaservice.web.dto.model.RoleDto;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

/**
 * User role mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface RoleMapper {

    /**
     * Maps role entity to dto model.
     *
     * @param role - role entity
     * @return role dto
     */
    RoleDto map(RoleEntity role);

    /**
     * Maps role entities set to dto list.
     *
     * @param roles - role entities set
     * @return dto roles list
     */
    List<RoleDto> mapRoles(Set<RoleEntity> roles);
}

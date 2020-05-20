package com.ecaservice.oauth.mapping;

import com.ecaservice.oauth.entity.RoleEntity;
import com.ecaservice.user.model.Role;
import com.ecaservice.web.dto.model.RoleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
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
     * Maps role to role dto.
     *
     * @param role - role object
     * @return role dto
     */
    @Mapping(source = "authority", target = "roleName")
    RoleDto map(GrantedAuthority role);

    /**
     * Map role entity ro role object.
     *
     * @param roleEntity - role entity
     * @return role object
     */
    @Mapping(source = "roleName", target = "authority")
    Role mapRole(RoleEntity roleEntity);

    /**
     * Maps roles to roles dto list.
     *
     * @param role - roles list
     * @return roles dto list
     */
    List<RoleDto> map(Collection<? extends GrantedAuthority> role);

    /**
     * Maps roles entities to roles list.
     *
     * @param roleEntitySet - roles entities set
     * @return roles list
     */
    List<Role> map(Set<RoleEntity> roleEntitySet);
}

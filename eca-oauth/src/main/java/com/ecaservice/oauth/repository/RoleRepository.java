package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository to manage with {@link RoleEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    /**
     * Gets role entity by name.
     *
     * @param roleName - role name
     * @return role entity
     */
    Optional<RoleEntity> findByRoleName(String roleName);
}

package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.UserProfileOptionsDataEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link UserProfileOptionsDataEventEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface UserProfileOptionsDataEventRepository extends JpaRepository<UserProfileOptionsDataEventEntity, Long> {
}

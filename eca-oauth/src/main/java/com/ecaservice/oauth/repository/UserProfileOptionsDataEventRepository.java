package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.UserProfileOptionsDataEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository to manage with {@link UserProfileOptionsDataEventEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface UserProfileOptionsDataEventRepository extends JpaRepository<UserProfileOptionsDataEventEntity, Long> {

    /**
     * Deleted user profile options data event.
     *
     * @param id - event id
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Modifying
    @Query("delete from UserProfileOptionsDataEventEntity u where u.id = :id")
    void deleteEvent(@Param("id") Long id);
}

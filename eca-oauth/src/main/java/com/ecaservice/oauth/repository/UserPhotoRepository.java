package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link UserPhoto} persistence entity.
 *
 * @author Roman Batygin
 */
public interface UserPhotoRepository extends JpaRepository<UserPhoto, Long> {
}

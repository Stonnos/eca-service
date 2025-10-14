package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.PersonalAccessTokenEntity;
import com.ecaservice.oauth.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link PersonalAccessTokenEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface PersonalAccessTokenRepository extends JpaRepository<PersonalAccessTokenEntity, Long> {

    /**
     * Gets personal access token entity by token value.
     *
     * @param token - token value
     * @return personal access token entity
     */
    PersonalAccessTokenEntity findByToken(String token);

    /**
     * Checks token name existing.
     *
     * @param userEntity - user entity
     * @param name       - token name
     * @return {@code true} if token name exists. {@code false} otherwise
     */
    boolean existsByUserEntityAndName(UserEntity userEntity, String name);

    /**
     * Finds user personal access tokens page.
     *
     * @param userEntity - user entity
     * @param pageable   - pageable
     * @return user personal access tokens page
     */
    Page<PersonalAccessTokenEntity> findByUserEntity(UserEntity userEntity, Pageable pageable);
}

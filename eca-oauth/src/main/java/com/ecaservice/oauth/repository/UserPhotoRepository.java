package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.entity.UserPhoto;
import com.ecaservice.oauth.projection.UserPhotoIdProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository to manage with {@link UserPhoto} persistence entity.
 *
 * @author Roman Batygin
 */
public interface UserPhotoRepository extends JpaRepository<UserPhoto, Long> {

    /**
     * Finds user photo.
     *
     * @param userEntity - user entity
     * @return user photo
     */
    UserPhoto findByUserEntity(UserEntity userEntity);

    /**
     * Gets user photo id for specified user.
     *
     * @param userEntity - user entity
     * @return user photo id
     */
    @Query("select uf.id from UserPhoto uf where uf.userEntity = :userEntity")
    Long getUserPhotoId(@Param("userEntity") UserEntity userEntity);

    /**
     * Gets users photo ids.
     *
     * @param users - users list
     * @return users photo ids list
     */
    @Query("select uf.id as id, uf.userEntity.id as userId from UserPhoto uf where uf.userEntity in (:users)")
    List<UserPhotoIdProjection> getUserPhotoIds(@Param("users") List<UserEntity> users);
}

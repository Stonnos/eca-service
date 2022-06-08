package com.ecaservice.oauth.projection;

/**
 * User photo projection.
 *
 * @author Roman Batygin
 */
public interface UserPhotoIdProjection {

    /**
     * Gets photo id.
     *
     * @return photo id
     */
    long getId();

    /**
     * Gets user id.
     *
     * @return user id
     */
    long getUserId();
}

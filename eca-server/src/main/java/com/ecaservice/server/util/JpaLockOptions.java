package com.ecaservice.server.util;

import lombok.experimental.UtilityClass;

/**
 * Jpa lock options.
 * @author Roman Batygin
 */
@UtilityClass
public class JpaLockOptions {

    /**
     * Indicates that rows which are already locked should be skipped.
     */
    public static final String SKIP_LOCKED = "-2";
}

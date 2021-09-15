package com.ecaservice.oauth.config.audit;

import lombok.experimental.UtilityClass;

/**
 * Audit codes.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class AuditCodes {

    /**
     * Enables 2FA
     */
    public static final String ENABLE_2FA = "ENABLE_2FA";

    /**
     * Disables 2FA
     */
    public static final String DISABLE_2FA = "DISABLE_2FA";

    /**
     * Updates user personal data
     */
    public static final String UPDATE_PERSONAL_DATA = "UPDATE_PERSONAL_DATA";

    /**
     * Updates user photo
     */
    public static final String UPDATE_PHOTO = "UPDATE_PHOTO";

    /**
     * Deletes user photo
     */
    public static final String DELETE_PHOTO = "DELETE_PHOTO";

    /**
     * Creates new user
     */
    public static final String CREATE_USER = "CREATE_USER";

    /**
     * Locks user
     */
    public static final String LOCK_USER = "LOCK_USER";

    /**
     * Unlocks user
     */
    public static final String UNLOCK_USER = "UNLOCK_USER";

    /**
     * Creates change password request
     */
    public static final String CREATE_CHANGE_PASSWORD_REQUEST = "CREATE_CHANGE_PASSWORD_REQUEST";

    /**
     * Confirm change password request
     */
    public static final String CONFIRM_CHANGE_PASSWORD_REQUEST = "CONFIRM_CHANGE_PASSWORD_REQUEST";

    /**
     * Creates reset password request
     */
    public static final String CREATE_RESET_PASSWORD_REQUEST = "CREATE_RESET_PASSWORD_REQUEST";

    /**
     * Reset password
     */
    public static final String RESET_PASSWORD = "RESET_PASSWORD";

    /**
     * Creates change email request
     */
    public static final String CREATE_CHANGE_EMAIL_REQUEST = "CREATE_CHANGE_EMAIL_REQUEST";

    /**
     * Confirm change email request
     */
    public static final String CONFIRM_CHANGE_EMAIL_REQUEST = "CONFIRM_CHANGE_EMAIL_REQUEST";
}

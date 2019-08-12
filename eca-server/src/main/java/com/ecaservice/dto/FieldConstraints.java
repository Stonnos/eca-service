package com.ecaservice.dto;

/**
 * Dto field constraints utility class.
 *
 * @author Roman Batygin
 */
public class FieldConstraints {

    /**
     * First name max size
     */
    public static final int FIRST_NAME_MAX_SIZE = 30;

    /**
     * Email max size
     */
    public static final int EMAIL_MAX_SIZE = 50;

    /**
     * Email regex
     */
    public static final String EMAIL_REGEX =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private FieldConstraints() {
    }
}

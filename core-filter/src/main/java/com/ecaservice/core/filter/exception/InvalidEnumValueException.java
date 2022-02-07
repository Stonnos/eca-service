package com.ecaservice.core.filter.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import org.apache.commons.lang3.EnumUtils;

/**
 * Invalid enum value exception.
 *
 * @author Roman Batygin
 */
public class InvalidEnumValueException extends ValidationErrorException {

    private static final String ERROR_CODE = "InvalidEnumValue";

    /**
     * Constructor with parameters.
     *
     * @param enumValue - enum value
     * @param enumClazz - enum class type
     */
    public InvalidEnumValueException(String enumValue, Class enumClazz) {
        super(ERROR_CODE, String.format("Invalid enum value [%s] for type [%s]. Expected one of %s", enumValue,
                enumClazz.getSimpleName(), EnumUtils.getEnumList(enumClazz)));
    }
}

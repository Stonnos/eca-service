package com.ecaservice.common.web.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * Mask utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class MaskUtils {

    private static final int NOT_MASKED_SYMBOLS_NUM = 4;
    private static final String MASK_SYMBOL = "*";

    /**
     * Masks all characters except the first four.
     *
     * @param value - string value
     */
    public static String mask(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        } else {
            return value.length() <= NOT_MASKED_SYMBOLS_NUM ? MASK_SYMBOL.repeat(value.length()) : internalMask(value);
        }
    }

    /**
     * Masks email characters before @ symbol from 2nd position.
     *
     * @param email - email value
     */
    public static String maskEmail(String email) {
        if (StringUtils.isNotEmpty(email)) {
            int aIndex = email.indexOf("@");
            if (aIndex > 0) {
                return email.charAt(0) + MASK_SYMBOL.repeat(aIndex - 1) + email.substring(aIndex);
            }
        }
        return email;
    }

    private static String internalMask(String value) {
        return value.substring(0, NOT_MASKED_SYMBOLS_NUM) + MASK_SYMBOL.repeat(value.length() - NOT_MASKED_SYMBOLS_NUM);
    }
}

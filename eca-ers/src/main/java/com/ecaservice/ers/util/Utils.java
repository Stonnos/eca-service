package com.ecaservice.ers.util;

import lombok.experimental.UtilityClass;

import java.math.BigInteger;
import java.util.Optional;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    /**
     * Returns integer value if specified big integer value is not null, null otherwise.
     *
     * @param value - big integer value
     * @return integer value
     */
    public static Integer toInteger(BigInteger value) {
        return Optional.ofNullable(value).map(BigInteger::intValue).orElse(null);
    }
}

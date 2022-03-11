package com.ecaservice.auto.test.util;

import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.base.model.MessageError;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    /**
     * Transform double value to scaled big decimal. Returns {@code null} in case if double value is NaN.
     *
     * @param value - double value
     * @param scale - scale value
     * @return big decimal value
     */
    public static BigDecimal getScaledValue(double value, int scale) {
        if (weka.core.Utils.isMissingValue(value)) {
            return null;
        }
        return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * Gets scaled decimal value.
     *
     * @param decimal - big decimal value
     * @param scale   - scale value
     * @return scaled decimal value
     */
    public static BigDecimal getScaledValue(BigDecimal decimal, int scale) {
        return Optional.ofNullable(decimal)
                .map(d -> d.setScale(scale, RoundingMode.HALF_UP))
                .orElse(null);
    }

    /**
     * Gets first response error message.
     *
     * @param ecaResponse - eca response
     * @return error message
     */
    public static String getFirstErrorMessage(EcaResponse ecaResponse) {
        return Optional.ofNullable(ecaResponse.getErrors())
                .map(messageErrors -> messageErrors.iterator().next())
                .map(MessageError::getMessage)
                .orElse(null);
    }
}

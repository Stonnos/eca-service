package com.ecaservice.data.storage.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class SqlUtils {

    /**
     * Gets string value from result set safe.
     *
     * @param result - result set
     * @param column - column index
     * @return string value
     * @throws SQLException in case of sql error
     */
    public static String getStringValueSafe(ResultSet result, int column) throws SQLException {
        String stringValue = result.getObject(column).toString().trim();
        return !StringUtils.isEmpty(stringValue) ? stringValue : null;
    }

    /**
     * Gets double value from result set.
     *
     * @param result - result set
     * @param column - column index
     * @return double value
     * @throws SQLException in case of sql error
     */
    public static double getDoubleValue(ResultSet result, int column) throws SQLException {
        return result.getBigDecimal(column).doubleValue();
    }

    /**
     * Gets local date time value from result set.
     *
     * @param result - result set
     * @param column - column index
     * @return double value
     * @throws SQLException in case of sql error
     */
    public static LocalDateTime getLocalDateTimeValue(ResultSet result, int column) throws SQLException {
        return result.getTimestamp(column).toLocalDateTime();
    }
}

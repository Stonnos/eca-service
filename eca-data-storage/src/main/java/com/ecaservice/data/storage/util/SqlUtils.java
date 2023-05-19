package com.ecaservice.data.storage.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

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
}

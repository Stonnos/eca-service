package com.ecaservice.data.storage.validation;

import com.ecaservice.data.storage.validation.annotations.UniqueTableName;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Unique table name validation.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class UniqueTableNameValidator implements ConstraintValidator<UniqueTableName, String> {

    private static final String TABLE_NOT_EXISTS_QUERY_FORMAT =
            "SELECT NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = '%s')";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean isValid(String tableName, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isBlank(tableName)) {
            return false;
        }
        Boolean result =
                jdbcTemplate.queryForObject(String.format(TABLE_NOT_EXISTS_QUERY_FORMAT, tableName.trim()), Boolean.class);
        return Boolean.TRUE.equals(result);
    }
}
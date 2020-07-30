package com.ecaservice.data.storage.validation;

import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.data.storage.validation.annotations.UniqueTableName;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.ecaservice.data.storage.config.Constants.INSTANCES_TABLE_NAME;

/**
 * Unique table name validation.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class UniqueTableNameValidator implements ConstraintValidator<UniqueTableName, String> {

    private final InstancesRepository instancesRepository;

    @Override
    public boolean isValid(String tableName, ConstraintValidatorContext constraintValidatorContext) {
        return StringUtils.isNotBlank(tableName) && !INSTANCES_TABLE_NAME.equals(tableName.trim()) &&
                !instancesRepository.existsByTableName(tableName.trim());
    }
}
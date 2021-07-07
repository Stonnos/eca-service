package com.ecaservice.core.filter.util;

import com.ecaservice.core.filter.exception.FieldNotFoundException;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.path.PathImpl;

import javax.validation.Path;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.util.ReflectionUtils.doWithFields;

/**
 * Reflection utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ReflectionUtils {

    /**
     * Cache key in format className.fieldName
     */
    private static final String KEY_FORMAT = "%s.%s";

    /**
     * Fields type cache
     */
    private static final ConcurrentHashMap<String, Class<?>> fieldClassMap = new ConcurrentHashMap<>(256);

    /**
     * Gets field type. Note: Field name can be composite, for example:
     * <pre>
     *  entity.entity1.prop1
     * </pre>
     * If the field name is composite, then the last property is taken. In our case the last property is prop1.
     *
     * @param fieldName - field name
     * @param clazz     - entity class
     * @return field type
     */
    public static Class<?> getFieldType(String fieldName, Class<?> clazz) {
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("Field name is blank string!");
        }
        Path path = PathImpl.createPathFromString(fieldName);
        return getTargetClazz(path, clazz);
    }

    private static Class<?> getTargetClazz(Path path, Class<?> clazz) {
        Class<?> currentClazz = clazz;
        for (Path.Node node : path) {
            currentClazz = getInternalFieldType(node.getName(), currentClazz);
        }
        return currentClazz;
    }

    private static Class<?> getInternalFieldType(String fieldName, Class<?> clazz) {
        String key = String.format(KEY_FORMAT, clazz.getName(), fieldName);
        Class<?> result = fieldClassMap.get(key);
        if (result == null) {
            doWithFields(clazz,
                    field -> fieldClassMap.putIfAbsent(key, field.getType()),
                    field -> fieldName.equals(field.getName()));
            result = fieldClassMap.get(key);
            if (result == null) {
                throw new FieldNotFoundException(fieldName, clazz);
            }
        }
        return result;
    }
}

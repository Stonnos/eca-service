package com.ecaservice.common.web.util;

import lombok.experimental.UtilityClass;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import javax.validation.Path;

/**
 * Bean utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class BeanUtil {

    /**
     * Invokes getter method for specified property. Note: Field name can be composite, for example:
     * <pre>
     *  entity.entity1.prop1
     * </pre>
     * If the field name is composite, then the last property is taken. In our case the last property is prop1.
     *
     * @param bean         - bean
     * @param propertyName - property name
     * @return result value
     */
    public static Object invokeGetter(Object bean, String propertyName) {
        Path path = PathImpl.createPathFromString(propertyName);
        Object methodResult = bean;
        for (var node : path) {
            methodResult = internalInvokeGetter(methodResult, node.getName());
            if (methodResult == null) {
                return null;
            }
        }
        return methodResult;
    }

    private static Object internalInvokeGetter(Object bean, String propertyName) {
        var propertyDescriptor = BeanUtils.getPropertyDescriptor(bean.getClass(), propertyName);
        Assert.notNull(propertyDescriptor,
                String.format("Property descriptor not found for property [%s] of bean [%s]", propertyName,
                        bean.getClass().getSimpleName()));
        var readMethod = propertyDescriptor.getReadMethod();
        Assert.notNull(readMethod, String.format("Read method not found for property [%s] of bean [%s]", propertyName,
                bean.getClass().getSimpleName()));
        return ReflectionUtils.invokeMethod(readMethod, bean);
    }
}

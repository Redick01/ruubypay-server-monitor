package io.redick.util;

import java.lang.reflect.Field;
import java.util.Objects;
import lombok.val;
import org.springframework.util.ReflectionUtils;

/**
 * @author Redick01
 */
public final class ReflectionUtil {

    private ReflectionUtil() {}

    public static Object getFieldValue(Class<?> targetClass, String fieldName, Object targetObj) {
        val field = getField(targetClass, fieldName);
        if (Objects.isNull(field)) {
            return null;
        }
        val fieldObj = ReflectionUtils.getField(field, targetObj);
        if (Objects.isNull(fieldObj)) {
            return null;
        }
        return fieldObj;
    }

    public static void setFieldValue(Class<?> targetClass, String fieldName, Object targetObj, Object targetVal) throws IllegalAccessException {
        val field = getField(targetClass, fieldName);
        if (Objects.isNull(field)) {
            return;
        }
        field.set(targetObj, targetVal);
    }

    public static Field getField(Class<?> targetClass, String fieldName) {
        Field field = ReflectionUtils.findField(targetClass, fieldName);
        if (Objects.isNull(field)) {
            return null;
        }
        ReflectionUtils.makeAccessible(field);
        return field;
    }
}

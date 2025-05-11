package com.orbithy.cms.utils;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
public class WrapperUtil {

    public static <T> UpdateWrapper<T> buildNonNullUpdateWrapper(T entity, String idField, Object idValue) {
        UpdateWrapper<T> wrapper = new UpdateWrapper<>();
        wrapper.eq(idField, idValue);

        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                Object value = field.get(entity);
                if (value != null && !(value instanceof String && ((String) value).isBlank())) {
                    String column = getColumnName(field);
                    wrapper.set(column, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return wrapper;
    }

    // 获取列名（优先使用 @TableField）
    private static String getColumnName(Field field) {
        TableField tableField = field.getAnnotation(TableField.class);
        if (tableField != null && !tableField.value().isEmpty()) {
            return tableField.value();
        }
        return camelToUnderline(field.getName());
    }

    // 下划线命名辅助方法
    private static String camelToUnderline(String str) {
        return str.replaceAll("([A-Z])", "_$1").toLowerCase();
    }
}

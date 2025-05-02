package com.orbithy.cms.utils;

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
                Object value = field.get(entity);
                if (value != null && !Modifier.isStatic(field.getModifiers())) {
                    wrapper.set(camelToUnderline(field.getName()), value); // 转换字段名为数据库字段
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return wrapper;
    }

    // 下划线命名辅助方法
    private static String camelToUnderline(String str) {
        return str.replaceAll("([A-Z])", "_$1").toLowerCase();
    }

}

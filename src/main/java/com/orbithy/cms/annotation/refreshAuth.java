package com.orbithy.cms.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于校验refresh token
 */
@Target(ElementType.METHOD) // 作用于方法
@Retention(RetentionPolicy.RUNTIME) // 运行时有效
public @interface refreshAuth {
}

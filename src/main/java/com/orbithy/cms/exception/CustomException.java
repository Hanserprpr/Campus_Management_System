package com.orbithy.cms.exception;

/**
 * 自定义业务异常
 */
public class CustomException extends RuntimeException {
    
    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }
} 
package com.orbithy.cms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "course-selection")
public class CourseSelectionConfig {
    private Map<String, SystemStatus> terms = new HashMap<>();

    @Data
    public static class SystemStatus {
        private boolean isOpen;
        private String status;
        private String statusDescription;
        private String updateTime;
    }
} 
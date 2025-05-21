package com.orbithy.cms.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import com.orbithy.cms.data.enums.Major;
import org.springframework.stereotype.Component;

@Component
public class MajorConverter implements Converter<String, Major> {

    @Override
    public Major convert(@NotNull String source) {
        for (Major major : Major.values()) {
            if (major.getLabel().equals(source)) {
                return major;
            }
        }
        throw new IllegalArgumentException("无法识别的专业: " + source);
    }
}

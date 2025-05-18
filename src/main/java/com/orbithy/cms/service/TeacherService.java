package com.orbithy.cms.service;

import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.exception.CustomException;
import com.orbithy.cms.mapper.ClassMapper;
import com.orbithy.cms.mapper.UserMapper;
import com.orbithy.cms.utils.ResponseUtil;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TeacherService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private ClassMapper classMapper;

    public ResponseEntity<Result> getMessage(String id) {
        if (userMapper.getPermission(id) !=1){
            throw new CustomException("不是老师身份");
        }
        Map<String, Integer> Mes = new HashMap<>();
        int classAmo = userMapper.getClassAmoByTeacherId(id);
        int totalClassHour = userMapper.getTotalClassHour(id);
        Mes.put("totalClassHour", totalClassHour);
        Mes.put("classAmo", classAmo);
        return ResponseUtil.build(Result.success(Mes,"CGA"));
    }

    public ResponseEntity<Result> getCountClass(String id) {
        Map<String, Integer> result = new HashMap<>();
        Integer activeClass = classMapper.getMyActiveClassCount(id);
        Integer pendingClass = classMapper.getMyPendingClassCount(id);
        result.put("activeClass", activeClass);
        result.put("pendingClass", pendingClass);
        return ResponseUtil.build(Result.success(result,"获取成功"));
    }
}

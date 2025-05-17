package com.orbithy.cms.service;

import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.exception.CustomException;
import com.orbithy.cms.mapper.UserMapper;
import com.orbithy.cms.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TeacherService {
    @Autowired
    UserMapper userMapper;
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
}

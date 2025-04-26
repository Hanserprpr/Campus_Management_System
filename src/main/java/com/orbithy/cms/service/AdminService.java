package com.orbithy.cms.service;

import com.orbithy.cms.data.po.Section;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.mapper.UserMapper;
import com.orbithy.cms.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private UserMapper userMapper;

    public ResponseEntity<Result> getTeacherList() {
        return ResponseUtil.build(Result.success(userMapper.getTeacherList(), "获取教师列表成功"));
    }
}

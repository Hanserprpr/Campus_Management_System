package com.orbithy.cms.service;

import com.orbithy.cms.data.dto.CreateCourseDTO;
import com.orbithy.cms.data.po.Grade;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.mapper.ClassMapper;
import com.orbithy.cms.mapper.GradeMapper;
import com.orbithy.cms.mapper.UserMapper;
import com.orbithy.cms.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GradeService {
    @Autowired
    private GradeMapper gradeMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ClassMapper classMapper;

    /**
     * 添加成绩
     */
    @Transactional
    public ResponseEntity<Result> setGrade(String id, Grade gradeDTO) {
        // 检查教师权限
        if (userMapper.getPermission(id) == 0) {
            return ResponseUtil.build(Result.error(401, "无权限"));
        }
        // 检查课程是否存在
        if (classMapper.getCourseById(gradeDTO.getCourseId()) == null) {
            return ResponseUtil.build(Result.error(404, "课程不存在"));
        }
        // 添加成绩
        gradeMapper.insert(gradeDTO);
        return ResponseUtil.build(Result.ok());
    }

    /**
     * 获取成绩列表
     */
    public ResponseEntity<Result> getGradeList(String id, String courseId) {
        // 检查教师权限
        if (userMapper.getPermission(id) == 0) {
            return ResponseUtil.build(Result.error(401, "无权限"));
        }
        if (classMapper.getTeacherIdByCourseId(courseId) == null) {
            return ResponseUtil.build(Result.error(404, "课程不存在"));
        }
        // 获取成绩列表
        return ResponseUtil.build(Result.success(gradeMapper.getGradeList(courseId), "获取成功"));
    }
}

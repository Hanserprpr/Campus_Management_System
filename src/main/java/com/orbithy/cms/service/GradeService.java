package com.orbithy.cms.service;

import com.orbithy.cms.data.dto.CreateCourseDTO;
import com.orbithy.cms.data.dto.GradeDTO;
import com.orbithy.cms.data.po.Grade;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.mapper.ClassMapper;
import com.orbithy.cms.mapper.GradeMapper;
import com.orbithy.cms.mapper.UserMapper;
import com.orbithy.cms.utils.ResponseUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        List<Grade> grades = gradeMapper.getGradeByCourseId(courseId);
        // 1. 获取所有学生 ID
        Set<String> studentIds = grades.stream()
                .map(Grade::getStudentId) // 返回 String 类型
                .collect(Collectors.toSet());


        // 2. 批量查询所有学生的用户名
        Map<Integer, String> studentUsernameMap = userMapper.getUsernamesByIds(new ArrayList<>(studentIds));

        // 3. 遍历 grades，直接从 studentUsernameMap 获取对应的 username
        List<GradeDTO> gradeDTOList = new ArrayList<>();
        for (Grade grade : grades) {
            GradeDTO dto = new GradeDTO();
            BeanUtils.copyProperties(grade, dto);
            dto.setUsername(studentUsernameMap.get(grade.getStudentId()));  // 从 Map 中获取 username
            gradeDTOList.add(dto);
        }

        return ResponseUtil.build(Result.success(gradeDTOList, "获取成功"));
    }

    /**
     * 发布成绩
     */
    public ResponseEntity<Result> releaseGrade(String courseId) {
        if (classMapper.getTeacherIdByCourseId(courseId) == null) {
            return ResponseUtil.build(Result.error(404, "课程不存在"));
        }
        // 发布成绩
        gradeMapper.releaseGrade(courseId);
        return ResponseUtil.build(Result.ok());
    }
}

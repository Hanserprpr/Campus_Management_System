package com.orbithy.cms.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.orbithy.cms.data.dto.GradeDTO;
import com.orbithy.cms.data.dto.GradeTermDTO;
import com.orbithy.cms.data.po.Grade;
import com.orbithy.cms.data.po.User;
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

import java.util.*;
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
        if (classMapper.isGradeReleased(gradeDTO.getCourseId())) {
            return ResponseUtil.build(Result.error(400, "成绩已发布，无法修改"));
        }
        if (gradeMapper.getGradeByCourseIdAndStudentId(gradeDTO.getCourseId(), gradeDTO.getStudentId())) {
            // 更新成绩
            UpdateWrapper<Grade> wrapper = new UpdateWrapper<>();
            wrapper.eq("student_id", gradeDTO.getStudentId());
            wrapper.eq("course_id", gradeDTO.getCourseId());

            gradeMapper.update(gradeDTO, wrapper);

            return ResponseUtil.build(Result.success(200, "成绩更新成功"));
        }
        if (gradeDTO.getRank() == null) {
            gradeDTO.setRank((byte) 0);
        }
        // 添加成绩
        gradeMapper.insert(gradeDTO);
        return ResponseUtil.build(Result.ok());
    }

    /**
     * 获取成绩列表
     */
    public ResponseEntity<Result> getGradeList(String id, int courseId) {
        // 检查教师权限
        if (userMapper.getPermission(id) == 0) {
            return ResponseUtil.build(Result.error(401, "无权限"));
        }
        if (classMapper.getTeacherIdByCourseId(courseId) == null) {
            return ResponseUtil.build(Result.error(404, "课程不存在"));
        }
        // 获取成绩列表
        List<Grade> grades = gradeMapper.getGradeByCourseId(courseId);
        if (grades == null || grades.isEmpty()) {
            return ResponseUtil.build(Result.success(Collections.emptyList(), "暂无成绩记录"));
        }
        // 1. 获取所有学生 ID
        Set<Integer> studentIds = grades.stream()
                .map(Grade::getStudentId)
                .collect(Collectors.toSet());

        System.out.println("grades" + grades);
        System.out.println("studentIds" + studentIds);

        // 2. 批量查询所有学生的用户名
        List<Integer> studentIdList = new ArrayList<>(studentIds);

        System.out.println("studentIdList" + studentIdList);

        Map<Integer, String> idToUsernameMap = userMapper.getUsersByIds(studentIdList)
                .stream()
                .collect(Collectors.toMap(
                        User::getId,
                        User::getUsername
                ));

        List<GradeDTO> gradeDTOList = new ArrayList<>();
        for (Grade grade : grades) {
            GradeDTO dto = new GradeDTO();
            BeanUtils.copyProperties(grade, dto);
            dto.setUsername(idToUsernameMap.get(grade.getStudentId()));
            gradeDTOList.add(dto);
        }
        return ResponseUtil.build(Result.success(gradeDTOList, "获取成功"));
    }

    /**
     * 发布成绩
     */
    @Transactional
    public ResponseEntity<Result> releaseGrade(int courseId) {
        if (classMapper.getTeacherIdByCourseId(courseId) == null) {
            return ResponseUtil.build(Result.error(404, "课程不存在"));
        }
        gradeMapper.updateRankByCourse(courseId);
        // 发布成绩
        gradeMapper.releaseGrade(courseId);
        return ResponseUtil.build(Result.ok());
    }

    public ResponseEntity<Result> getGrade(String id, String term) {
        List<GradeTermDTO> grade = gradeMapper.getGradeByTerm(id, term);
        return ResponseUtil.build(Result.success(grade, "获取成绩成功"));
    }

    public ResponseEntity<Result> getMessage(String id) {
        int finish  = gradeMapper.finish(id);
        int unFinish = gradeMapper.unFinish(id);
        Map<String, Integer> result = new HashMap<>();
        result.put("finish", finish);
        result.put("unFinish", unFinish);

        return ResponseUtil.build(Result.success(result,"获取成功"));
    }
}

package com.orbithy.cms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.orbithy.cms.data.dto.CreateCourseDTO;
import com.orbithy.cms.data.po.Classes;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.mapper.ClassMapper;
import com.orbithy.cms.mapper.UserMapper;
import com.orbithy.cms.utils.ResponseUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassService {
    @Autowired
    private ClassMapper classMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 教师创建课程
     */
    public ResponseEntity<Result> createCourse(String userId, CreateCourseDTO courseDTO) {
        try {
            // 验证教师权限
            if (userMapper.getPermission(userId) != 1) {
                return ResponseUtil.build(Result.error(403, "无权限创建课程"));
            }

            // 创建课程对象
            Classes course = new Classes();
            BeanUtils.copyProperties(courseDTO, course);
            course.setTeacherId(Integer.parseInt(userId));
            course.setStatus(Classes.CourseStatus.PENDING);
            
            // 转换课程类型
            try {
                course.setType(Classes.CourseType.valueOf(courseDTO.getType()));
            } catch (IllegalArgumentException e) {
                return ResponseUtil.build(Result.error(400, "无效的课程类型"));
            }

            // 设置时间段
            course.setTimeSet(courseDTO.getTime());

            // 验证课程数据
            if (!course.isValidTime() || !course.isValidCapacity() || !course.isValidTerm() || !course.isValidWeeks()) {
                return ResponseUtil.build(Result.error(400, "课程信息不合法"));
            }

            // 转换时间段为字符串
            course.convertTimeSetToString();

            // 保存课程
            classMapper.insert(course);
            return ResponseUtil.build(Result.success(null, "课程创建成功，等待审批"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(500, "创建课程失败：" + e.getMessage()));
        }
    }

    /**
     * 教务审批课程
     */
    public ResponseEntity<Result> approveCourse(String adminId, Integer courseId, Integer status, String classNum) {
        try {
            // 验证教务权限
            if (userMapper.getPermission(adminId) != 0) {
                return ResponseUtil.build(Result.error(403, "无权限审批课程"));
            }

            // 验证状态值
            if (status != 1 && status != 2) {
                return ResponseUtil.build(Result.error(400, "无效的审批状态"));
            }

            Classes course = classMapper.getCourseById(courseId);
            if (course == null) {
                return ResponseUtil.build(Result.error(404, "课程不存在"));
            }

            // 验证课程状态
            if (course.getStatus() != Classes.CourseStatus.PENDING) {
                return ResponseUtil.build(Result.error(400, "只能审批待审批的课程"));
            }

            // 如果审批通过，验证课序号
            if (status == 1) {
                if (classNum == null || classNum.trim().isEmpty()) {
                    return ResponseUtil.build(Result.error(400, "审批通过时必须提供课序号"));
                }

                // 验证课序号唯一性
                QueryWrapper<Classes> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("class_num", classNum)
                        .eq("term", course.getTerm());
                if (classMapper.selectCount(queryWrapper) > 0) {
                    return ResponseUtil.build(Result.error(400, "该学期已存在相同课序号"));
                }

                // 设置课序号
                course.setClassNum(classNum);
            }

            // 更新课程状态和课序号
            classMapper.updateCourseStatusAndClassNum(courseId, status, classNum);
            String message = status == 1 ? "课程审批通过" : "课程审批拒绝";
            return ResponseUtil.build(Result.success(null, message));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(500, "审批失败：" + e.getMessage()));
        }
    }

    /**
     * 获取课程列表
     */
    public ResponseEntity<Result> getCourseList(String userId, String term) {
        try {
            // 验证学期格式
            if (term != null && !term.matches("\\d{4}-\\d{4}-[12]")) {
                return ResponseUtil.build(Result.error(400, "无效的学期格式"));
            }

            Integer permission = userMapper.getPermission(userId);
            List<Classes> courses;

            switch (permission) {
                case 0: // 教务
                    courses = term != null ? 
                            classMapper.getCoursesByTerm(term) : 
                            classMapper.selectList(null);
                    break;
                case 1: // 教师
                    courses = term != null ? 
                            classMapper.getTeacherCoursesByTerm(Integer.parseInt(userId), term) : 
                            classMapper.getTeacherCourses(Integer.parseInt(userId));
                    break;
                default:
                    return ResponseUtil.build(Result.error(403, "无效的用户权限"));
            }

            // 转换时间段格式
            courses.forEach(Classes::convertStringToTimeSet);

            return ResponseUtil.build(Result.success(courses, "获取课程列表成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(500, "获取课程列表失败：" + e.getMessage()));
        }
    }

    /**
     * 获取课程详情
     */
    public ResponseEntity<Result> getCourseDetail(String userId, Integer courseId) {
        try {
            Classes course = classMapper.getCourseById(courseId);
            if (course == null) {
                return ResponseUtil.build(Result.error(404, "课程不存在"));
            }

            // 验证权限
            Integer permission = userMapper.getPermission(userId);
            if (permission != 0 && // 教务
                !course.getTeacherId().toString().equals(userId)) { // 课程创建者
                return ResponseUtil.build(Result.error(403, "无权限查看此课程"));
            }

            // 转换时间段格式
            course.convertStringToTimeSet();

            return ResponseUtil.build(Result.success(course, "获取成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(500, "获取课程详情失败：" + e.getMessage()));
        }
    }

    /**
     * 更新课程
     */
    public ResponseEntity<Result> updateCourse(String userId, Integer courseId, CreateCourseDTO courseDTO) {
        try {
            // 验证教师权限和所有权
            Classes course = classMapper.getCourseById(courseId);
            if (course == null) {
                return ResponseUtil.build(Result.error(404, "课程不存在"));
            }
            if (!course.getTeacherId().toString().equals(userId)) {
                return ResponseUtil.build(Result.error(403, "无权限修改此课程"));
            }

            // 验证课程状态
            if (course.getStatus() != Classes.CourseStatus.PENDING) {
                return ResponseUtil.build(Result.error(403, "只能修改待审批的课程"));
            }

            // 更新课程信息
            BeanUtils.copyProperties(courseDTO, course);
            try {
                course.setType(Classes.CourseType.valueOf(courseDTO.getType()));
            } catch (IllegalArgumentException e) {
                return ResponseUtil.build(Result.error(400, "无效的课程类型"));
            }

            // 设置时间段
            course.setTimeSet(courseDTO.getTime());

            // 验证课程数据
            if (!course.isValidTime() || !course.isValidCapacity() || !course.isValidTerm() || !course.isValidWeeks()) {
                return ResponseUtil.build(Result.error(400, "课程信息不合法"));
            }

            // 转换时间段为字符串
            course.convertTimeSetToString();

            classMapper.updateById(course);
            return ResponseUtil.build(Result.success(null, "更新成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(500, "更新课程失败：" + e.getMessage()));
        }
    }

    /**
     * 删除课程
     */
    public ResponseEntity<Result> deleteCourse(String userId, Integer courseId) {
        try {
            Classes course = classMapper.getCourseById(courseId);
            if (course == null) {
                return ResponseUtil.build(Result.error(404, "课程不存在"));
            }
            if (!course.getTeacherId().toString().equals(userId)) {
                return ResponseUtil.build(Result.error(403, "无权限删除此课程"));
            }

            // 验证课程状态
            if (course.getStatus() != Classes.CourseStatus.PENDING) {
                return ResponseUtil.build(Result.error(403, "只能删除待审批的课程"));
            }

            classMapper.deleteById(courseId);
            return ResponseUtil.build(Result.success(null, "删除成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(500, "删除课程失败：" + e.getMessage()));
        }
    }
}

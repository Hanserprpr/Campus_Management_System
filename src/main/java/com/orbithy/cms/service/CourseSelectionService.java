package com.orbithy.cms.service;

import com.orbithy.cms.annotation.Admin;
import com.orbithy.cms.config.CourseSelectionConfig;
import com.orbithy.cms.data.po.Classes;
import com.orbithy.cms.data.po.CourseSelection;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.mapper.ClassMapper;
import com.orbithy.cms.mapper.CourseSelectionMapper;
import com.orbithy.cms.mapper.UserMapper;
import com.orbithy.cms.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
public class CourseSelectionService {
    @Autowired
    private CourseSelectionMapper courseSelectionMapper;
    @Autowired
    private ClassMapper classMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CourseSelectionConfig courseSelectionConfig;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 开始选课
     */
    @Admin
    public ResponseEntity<Result> startSelection(String term) {
        try {
            // 验证学期格式
            if (!term.matches("\\d{4}-\\d{4}-[12]")) {
                return ResponseUtil.build(Result.error(400, "无效的学期格式"));
            }

            // 检查是否已有选课记录
            CourseSelectionConfig.SystemStatus status = courseSelectionConfig.getTerms().get(term);
            if (status != null && status.isOpen()) {
                return ResponseUtil.build(Result.error(400, "选课系统已经启动"));
            }

            // 创建或更新系统状态
            CourseSelectionConfig.SystemStatus newStatus = new CourseSelectionConfig.SystemStatus();
            newStatus.setOpen(true);
            newStatus.setStatus("OPEN");
            newStatus.setStatusDescription("开放");
            newStatus.setUpdateTime(LocalDateTime.now().format(formatter));
            
            courseSelectionConfig.getTerms().put(term, newStatus);

            return ResponseUtil.build(Result.success(null, "选课系统启动成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(500, "启动选课系统失败：" + e.getMessage()));
        }
    }

    /**
     * 结束选课
     */
    @Admin
    public ResponseEntity<Result> endSelection(String term) {
        try {
            // 检查系统状态
            CourseSelectionConfig.SystemStatus status = courseSelectionConfig.getTerms().get(term);
            if (status == null || !status.isOpen()) {
                return ResponseUtil.build(Result.error(400, "选课系统未启动"));
            }

            // 更新系统状态
            status.setOpen(false);
            status.setStatus("CLOSED");
            status.setStatusDescription("关闭");
            status.setUpdateTime(LocalDateTime.now().format(formatter));

            return ResponseUtil.build(Result.success(null, "选课系统关闭成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(500, "关闭选课系统失败：" + e.getMessage()));
        }
    }

    /**
     * 获取选课系统状态
     */
    public boolean isSelectionOpen(String term) {
        CourseSelectionConfig.SystemStatus status = courseSelectionConfig.getTerms().get(term);
        return status != null && status.isOpen();
    }

    /**
     * 搜索课程
     */
    public ResponseEntity<Result> searchCourses(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            List<Classes> courses = classMapper.getAllClasses();
            return ResponseUtil.build(Result.success(courses,"搜索成功" ));
        }

        List<Classes> courses = classMapper.searchCourses(keyword);
        return ResponseUtil.build(Result.success(courses, "搜索成功"));
    }

    /**
     * 选课
     */
    public ResponseEntity<Result> selectCourse(Integer studentId, Integer courseId) {
        try {
            // 验证课程是否存在
            Classes course = classMapper.getCourseById(courseId);
            if (course == null) {
                return ResponseUtil.build(Result.error(404, "课程不存在"));
            }

            // 验证课程状态
            if (course.getStatus().getCode() != 1) { // 1表示已通过审批
                return ResponseUtil.build(Result.error(400, "课程未通过审批"));
            }

            // 检查是否已选
            CourseSelection existingSelection = courseSelectionMapper.getSelection(studentId, courseId);
            if (existingSelection != null) {
                return ResponseUtil.build(Result.error(400, "已经选择过该课程"));
            }

            // 检查课程容量
            int currentCount = courseSelectionMapper.getCourseSelectionCount(courseId);
            if (currentCount >= course.getCapacity()) {
                return ResponseUtil.build(Result.error(400, "课程已满"));
            }

            // 检查总学分
            Integer currentPoints = courseSelectionMapper.getTotalPoints(studentId);
            if (currentPoints == null) currentPoints = 0;
            if (currentPoints + course.getPoint() > 35) {
                return ResponseUtil.build(Result.error(400, "总学分超过35分"));
            }

            // 检查时间冲突
            Set<Integer> courseTimeSet = course.getTimeSet();
            List<CourseSelection> studentSelections = courseSelectionMapper.getStudentSelections(studentId);
            
            for (CourseSelection selection : studentSelections) {
                Classes selectedCourse = classMapper.getCourseById(selection.getCourseId());
                Set<Integer> selectedTimeSet = selectedCourse.getTimeSet();
                
                for (Integer time : courseTimeSet) {
                    if (selectedTimeSet.contains(time)) {
                        return ResponseUtil.build(Result.error(400, "存在时间冲突"));
                    }
                }
            }

            // 创建选课记录
            CourseSelection selection = new CourseSelection();
            selection.setStudentId(studentId);
            selection.setCourseId(courseId);
            selection.setClassNum(course.getClassNum());
            
            courseSelectionMapper.insert(selection);
            return ResponseUtil.build(Result.success(null, "选课成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(500, "选课失败：" + e.getMessage()));
        }
    }

    /**
     * 查询选课结果
     */
    public ResponseEntity<Result> getSelectionResults(Integer studentId) {
        try {
            List<CourseSelection> selections = courseSelectionMapper.getStudentSelections(studentId);
            
            // 获取课程详情
            for (CourseSelection selection : selections) {
                Classes course = classMapper.getCourseById(selection.getCourseId());
                if (course != null) {
                    course.convertStringToTimeSet();
                }
            }

            return ResponseUtil.build(Result.success(selections, "查询成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(500, "查询失败：" + e.getMessage()));
        }
    }

    /**
     * 退选课程
     */
    public ResponseEntity<Result> dropCourse(Integer studentId, Integer courseId) {
        try {
            // 验证课程是否存在
            Classes course = classMapper.getCourseById(courseId);
            if (course == null) {
                return ResponseUtil.build(Result.error(404, "课程不存在"));
            }

            // 检查选课记录是否存在
            CourseSelection selection = courseSelectionMapper.getSelection(studentId, courseId);
            if (selection == null) {
                return ResponseUtil.build(Result.error(404, "未选择该课程"));
            }

            // 检查选课系统是否开放
            if (!isSelectionOpen(course.getTerm())) {
                return ResponseUtil.build(Result.error(400, "选课系统未开放，无法退选"));
            }

            // 删除选课记录
            courseSelectionMapper.deleteById(selection.getId());
            return ResponseUtil.build(Result.success(null, "退选成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(500, "退选失败：" + e.getMessage()));
        }
    }
} 
package com.orbithy.cms.service;

import com.orbithy.cms.annotation.Admin;
import com.orbithy.cms.config.CourseSelectionConfig;
import com.orbithy.cms.data.po.Classes;
import com.orbithy.cms.data.po.CourseSelection;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.exception.CustomException;
import com.orbithy.cms.mapper.ClassMapper;
import com.orbithy.cms.mapper.CourseSelectionMapper;
import com.orbithy.cms.mapper.UserMapper;
import com.orbithy.cms.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
                throw new CustomException("无效的学期格式");
            }

            // 检查是否已有选课记录
            CourseSelectionConfig.SystemStatus status = courseSelectionConfig.getTerms().get(term);
            if (status != null && status.isOpen()) {
                throw new CustomException("选课系统已经启动");
            }

            // 创建或更新系统状态
            CourseSelectionConfig.SystemStatus newStatus = new CourseSelectionConfig.SystemStatus();
            newStatus.setOpen(true);
            newStatus.setStatus("OPEN");
            newStatus.setStatusDescription("开放");
            newStatus.setUpdateTime(LocalDateTime.now().format(formatter));
            
            courseSelectionConfig.getTerms().put(term, newStatus);

            return ResponseUtil.build(Result.success(null, "选课系统启动成功"));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("启动选课系统失败：" + e.getMessage(), e);
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
                throw new CustomException("选课系统未启动");
            }

            // 更新系统状态
            status.setOpen(false);
            status.setStatus("CLOSED");
            status.setStatusDescription("关闭");
            status.setUpdateTime(LocalDateTime.now().format(formatter));

            return ResponseUtil.build(Result.success(null, "选课系统关闭成功"));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("关闭选课系统失败：" + e.getMessage(), e);
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
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                List<Classes> courses = classMapper.getAllClasses();
                return ResponseUtil.build(Result.success(courses,"搜索成功" ));
            }

            List<Classes> courses = classMapper.searchCourses(keyword);
            return ResponseUtil.build(Result.success(courses, "搜索成功"));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("搜索课程失败：" + e.getMessage(), e);
        }
    }

    /**
     * 选课
     */
    public ResponseEntity<Result> selectCourse(String studentId, Integer courseId) {
        try {
            // 验证课程是否存在
            Classes course = classMapper.getCourseById(courseId);
            if (course == null) {
                throw new CustomException("课程不存在");
            }

            // 验证选课系统是否开放
            if (!isSelectionOpen(course.getTerm())) {
                throw new CustomException("选课系统未开放");
            }

            // 验证课程状态
            if (course.getStatus().getCode() != 1) { // 1表示已通过审批
                throw new CustomException("课程未通过审批");
            }

            // 检查是否已选
            CourseSelection existingSelection = courseSelectionMapper.getSelection(Integer.parseInt(studentId), courseId);
            if (existingSelection != null) {
                throw new CustomException("已经选择过该课程");
            }

            // 检查课程容量
            int currentCount = courseSelectionMapper.getCourseSelectionCount(courseId);
            if (currentCount >= course.getCapacity()) {
                throw new CustomException("课程已满");
            }

            // 检查总学分
            Integer currentPoints = courseSelectionMapper.getTotalPoints(Integer.parseInt(studentId));
            if (currentPoints == null) currentPoints = 0;
            if (currentPoints + course.getPoint() > 35) {
                throw new CustomException("总学分超过35分");
            }

            // 检查时间冲突
            List<CourseSelection> studentSelections = courseSelectionMapper.getStudentSelections(Integer.parseInt(studentId));
            checkTimeConflict(course, studentSelections);

            // 创建选课记录
            CourseSelection selection = new CourseSelection();
            selection.setStudentId(Integer.parseInt(studentId));
            selection.setCourseId(courseId);
            selection.setClassNum(course.getClassNum());
            
            courseSelectionMapper.insert(selection);
            return ResponseUtil.build(Result.success(null, "选课成功"));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("选课失败：" + e.getMessage(), e);
        }
    }

    /**
     * 检查时间冲突
     * 考虑课程的周次范围和时间段
     */
    private void checkTimeConflict(Classes newCourse, List<CourseSelection> existingSelections) {
        // 获取新课程的时间段和周次范围
        List<Integer> newTimeSlots = parseTimeSlots(newCourse.getTime());
        int newStartWeek = newCourse.getWeekStart();
        int newEndWeek = newCourse.getWeekEnd();

        for (CourseSelection selection : existingSelections) {
            Classes existingCourse = classMapper.getCourseById(selection.getCourseId());
            if (existingCourse == null) continue;

            // 检查周次是否有重叠
            int existingStartWeek = existingCourse.getWeekStart();
            int existingEndWeek = existingCourse.getWeekEnd();
            
            // 如果两个课程的周次没有重叠，则不冲突
            if (newEndWeek < existingStartWeek || newStartWeek > existingEndWeek) {
                continue;
            }

            // 如果周次重叠，则检查时间段是否冲突
            List<Integer> existingTimeSlots = parseTimeSlots(existingCourse.getTime());
            for (Integer timeSlot : newTimeSlots) {
                if (existingTimeSlots.contains(timeSlot)) {
                    throw new CustomException(String.format(
                        "与课程《%s》在第%d周至第%d周的星期%d第%d节存在时间冲突",
                        existingCourse.getName(),
                        Math.max(newStartWeek, existingStartWeek),
                        Math.min(newEndWeek, existingEndWeek),
                        timeSlot / 5 + 1,
                        timeSlot % 5 + 1
                    ));
                }
            }
        }
    }

    /**
     * 解析时间字符串为时间段列表
     * 将时间字符串（如"1,2,3"）转换为时间段列表
     */
    private List<Integer> parseTimeSlots(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(timeStr.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    /**
     * 查询选课结果
     */
    public ResponseEntity<Result> getSelectionResults(String studentId) {
        try {
            List<CourseSelection> selections = courseSelectionMapper.getStudentSelections(Integer.parseInt(studentId));
            
            // 获取课程详情
            for (CourseSelection selection : selections) {
                Classes course = classMapper.getCourseById(selection.getCourseId());

            }

            return ResponseUtil.build(Result.success(selections, "查询成功"));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("查询失败：" + e.getMessage(), e);
        }
    }

    /**
     * 退选课程
     */
    public ResponseEntity<Result> dropCourse(String studentId, Integer courseId) {
        try {
            // 验证课程是否存在
            Classes course = classMapper.getCourseById(courseId);
            if (course == null) {
                throw new CustomException("课程不存在");
            }

            // 检查选课记录是否存在
            CourseSelection selection = courseSelectionMapper.getSelection(Integer.parseInt(studentId), courseId);
            if (selection == null) {
                throw new CustomException("未选择该课程");
            }

            // 检查选课系统是否开放
            if (!isSelectionOpen(course.getTerm())) {
                throw new CustomException("选课系统未开放，无法退选");
            }

            // 删除选课记录
            courseSelectionMapper.deleteById(selection.getId());
            return ResponseUtil.build(Result.success(null, "退选成功"));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("退选失败：" + e.getMessage(), e);
        }
    }
} 
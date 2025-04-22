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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClassService {
    @Autowired
    private ClassMapper classMapper;
    @Autowired
    private UserMapper userMapper;

    // 定义每天的时间段范围
    private static final int SLOTS_PER_DAY = 5;  // 每天5个时间段
    private static final int DAYS = 5;           // 5个工作日
    
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
    public ResponseEntity<Result> approveCourse(String adminId, Integer courseId, Integer status, String classNum, String reason) {
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

            // 如果审批通过
            if (status == 1) {
                // 获取当前课程的课序号
                String existingClassNum = course.getClassNum();
                
                // 如果课程没有课序号，且审批时也没提供课序号
                if ((existingClassNum == null || existingClassNum.trim().isEmpty()) 
                    && (classNum == null || classNum.trim().isEmpty())) {
                    return ResponseUtil.build(Result.error(400, "课程没有课序号，审批通过时必须提供课序号"));
                }
                
                // 如果提供了新的课序号，使用新课序号；否则保留原课序号
                String finalClassNum = (classNum != null && !classNum.trim().isEmpty()) ? classNum : existingClassNum;
                
                // 如果课序号发生变化，检查唯一性
                if (!finalClassNum.equals(existingClassNum)) {
                    // 验证课序号唯一性
                    QueryWrapper<Classes> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("class_num", finalClassNum)
                        .eq("term", course.getTerm());
                    if (classMapper.selectCount(queryWrapper) > 0) {
                        return ResponseUtil.build(Result.error(400, "该学期已存在相同课序号"));
                    }
                }
                
                // 更新课程状态和课序号（如果有变化）
                classMapper.updateCourseStatusAndClassNum(courseId, status, finalClassNum, null);
            } else if (status == 2) {
                // 拒绝时更新状态和拒绝理由
                classMapper.updateCourseStatusAndClassNum(courseId, status, null, reason);
            }

            // 如果拒绝，验证拒绝理由
            if (status == 2 && (reason == null || reason.trim().isEmpty())) {
                return ResponseUtil.build(Result.error(400, "拒绝时必须提供拒绝理由"));
            }

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

    /**
     * 获取待批准的课程列表
     */
    public ResponseEntity<Result> getPendingCourses(String adminId) {
        try {
            // 验证教务权限
            if (userMapper.getPermission(adminId) != 0) {
                return ResponseUtil.build(Result.error(403, "无权限查看待批准课程"));
            }

            List<Classes> pendingCourses = classMapper.getPendingCourses();
            return ResponseUtil.build(Result.success(pendingCourses, "获取待批准课程成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(500, "获取待批准课程失败：" + e.getMessage()));
        }
    }

    /**
     * 生成排课方案（贪心算法）
     * 0-4: 周一的时间段
     * 5-9: 周二的时间段
     * 10-14: 周三的时间段
     * 15-19: 周四的时间段
     * 20-24: 周五的时间段
     */
    private boolean generateSchedule(List<Classes> courses) {
        // 按课时数量降序排序
        courses.sort((a, b) -> b.getPeriod() - a.getPeriod());

        // 创建时间段占用表 [天数][时间段]
        boolean[][] timeSlotOccupied = new boolean[DAYS][SLOTS_PER_DAY];
        // 创建教师课程表 [教师ID][天数][时间段]
        Map<Integer, boolean[][]> teacherSchedule = new HashMap<>();

        for (Classes course : courses) {
            boolean scheduled = false;
            int teacherId = course.getTeacherId();
            
            // 初始化教师课程表
            if (!teacherSchedule.containsKey(teacherId)) {
                teacherSchedule.put(teacherId, new boolean[DAYS][SLOTS_PER_DAY]);
            }

            // 需要安排的课时数
            int remainingPeriods = course.getPeriod();
            Set<Integer> assignedSlots = new HashSet<>();

            // 为每个课时寻找合适的时间段
            while (remainingPeriods > 0) {
                boolean foundSlot = false;

                // 遍历每一天
                for (int day = 0; day < DAYS && !foundSlot; day++) {
                    // 遍历每天的时间段
                    for (int slot = 0; slot < SLOTS_PER_DAY; slot++) {
                        // 计算实际的时间段编号
                        int actualSlot = day * SLOTS_PER_DAY + slot;
                        
                        // 检查该时间段是否可用
                        if (!timeSlotOccupied[day][slot] && 
                            !teacherSchedule.get(teacherId)[day][slot] &&
                            !assignedSlots.contains(actualSlot)) {
                            
                            // 分配时间段
                            timeSlotOccupied[day][slot] = true;
                            teacherSchedule.get(teacherId)[day][slot] = true;
                            assignedSlots.add(actualSlot);
                            remainingPeriods--;
                            foundSlot = true;
                            break;
                        }
                    }
                }

                // 如果找不到可用时间段，排课失败
                if (!foundSlot) {
                    return false;
                }
            }

            // 更新课程时间段
            course.setTime(convertSetToString(assignedSlots));
            classMapper.updateCourseTime(course.getId(), course.getTime());
        }
        
        return true;
    }

    /**
     * 将Set<Integer>转换为time字段的字符串格式
     */
    private String convertSetToString(Set<Integer> timeSlots) {
        return timeSlots.stream()
                       .sorted()
                       .map(String::valueOf)
                       .collect(Collectors.joining(","));
    }

    /**
     * 获取时间段对应的星期和节次
     */
    private String getTimeSlotInfo(int slot) {
        int day = slot / SLOTS_PER_DAY + 1;  // 1-5表示周一到周五
        int period = slot % SLOTS_PER_DAY + 1;  // 1-5表示第几节课
        return String.format("周%d第%d节", day, period);
    }

    /**
     * 自动排课
     */
    public ResponseEntity<Result> autoSchedule(String adminId, String term) {
        try {
            // 验证教务权限
            if (userMapper.getPermission(adminId) != 0) {
                return ResponseUtil.build(Result.error(403, "无权限进行自动排课"));
            }

            // 获取需要排课的课程（状态为已通过审批的课程）
            List<Classes> courses = classMapper.getCoursesByTermAndStatus(term, 1);
            
            // 执行排课
            boolean success = generateSchedule(courses);
            
            if (!success) {
                return ResponseUtil.build(Result.error(400, "无法找到合适的排课方案"));
            }

            // 生成排课结果报告
            StringBuilder report = new StringBuilder("排课成功！\n");
            for (Classes course : courses) {
                report.append(String.format("课程：%s\n", course.getName()));
                Arrays.stream(course.getTime().split(","))
                      .map(Integer::parseInt)
                      .forEach(slot -> report.append(getTimeSlotInfo(slot)).append("\n"));
                report.append("\n");
            }

            return ResponseUtil.build(Result.success(report.toString(), "自动排课完成"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(500, "自动排课失败：" + e.getMessage()));
        }
    }
}

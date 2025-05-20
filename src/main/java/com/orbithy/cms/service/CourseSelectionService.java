package com.orbithy.cms.service;

import com.orbithy.cms.data.dto.CourseSelectionResultDTO;
import com.orbithy.cms.data.dto.StudentListDTO;
import com.orbithy.cms.data.po.Classes;
import com.orbithy.cms.data.po.CourseSelection;
import com.orbithy.cms.data.po.Status;
import com.orbithy.cms.data.po.User;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.exception.CustomException;
import com.orbithy.cms.mapper.ClassMapper;
import com.orbithy.cms.mapper.CourseSelectionMapper;
import com.orbithy.cms.mapper.UserMapper;
import com.orbithy.cms.mapper.StatusMapper;
import com.orbithy.cms.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.orbithy.cms.service.TermService.getCurrentTerm;
import static com.orbithy.cms.service.TermService.isTermOpen;

@Service
public class CourseSelectionService {
    @Autowired
    private CourseSelectionMapper courseSelectionMapper;
    @Autowired
    private ClassMapper classMapper;
    @Autowired
    private UserMapper userMapper;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 搜索课程
     */
    public ResponseEntity<Result> searchCourses(String userId, String keyword, String term, String type) throws IOException {
        if (Objects.isNull(term)) {
            term = getCurrentTerm();
        }
        try {
            // 如果 keyword 为空，统一处理为 null
            if (keyword != null && keyword.trim().isEmpty()) {
                keyword = null;
            }

            // 如果 term 为空串，处理为 null
            if (term != null && term.trim().isEmpty()) {
                term = null;
            }

            // 如果 type 为空串，处理为 null
            if (type != null && type.trim().isEmpty()) {
                type = null;
            }

            List<Classes> courses = classMapper.searchCourses(Integer.valueOf(userId), keyword, term, type);
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
            if (!isTermOpen(course.getTerm())) {
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
            List<CourseSelectionResultDTO> resultList = new ArrayList<>();

            // 获取课程详情
            for (CourseSelection selection : selections) {
                Classes course = classMapper.getCourseById(selection.getCourseId());
                if (course != null) {
                    CourseSelectionResultDTO dto = new CourseSelectionResultDTO();
                    dto.setId(course.getId());
                    dto.setClassNum(course.getClassNum());
                    dto.setName(course.getName());
                    dto.setPoint(course.getPoint());
                    dto.setType(String.valueOf(course.getType()));
                    dto.setTime(course.getTime());
                    dto.setClassroom(course.getClassroom());
                    dto.setCapacity(course.getCapacity());
                    dto.setCategory(course.getCategory());

                    // 获取教师名称
                    String teacherName = userMapper.getUsernameById(course.getTeacherId());
                    dto.setTeacherName(teacherName);

                    // 获取已选人数
                    Integer selectedCount = classMapper.countCourseByCourseId(course.getId());
                    dto.setSelectedCount(selectedCount != null ? selectedCount : 0);

                    resultList.add(dto);
                }
            }

            return ResponseUtil.build(Result.success(resultList, "查询成功"));
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
            if (!isTermOpen(course.getTerm())) {
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

    public ResponseEntity<Result> getUnSelectResult(String userId) throws IOException {
        String term = getCurrentTerm();
        System.out.println(term);
        try {
            // 获取学生已选课程ID列表
            List<CourseSelection> selections = courseSelectionMapper.getStudentSelections(Integer.parseInt(userId));
            Set<Integer> selectedCourseIds = selections.stream()
                    .map(CourseSelection::getCourseId)
                    .collect(Collectors.toSet());

            // 获取指定学期的所有课程

            List<Classes> allCourses = classMapper.getCourseByTerm(term);
            List<CourseSelectionResultDTO> resultList = new ArrayList<>();

            // 过滤出未选课程并转换为DTO
            for (Classes course : allCourses) {
                if (!selectedCourseIds.contains(course.getId())) {
                    CourseSelectionResultDTO dto = new CourseSelectionResultDTO();
                    dto.setId(course.getId());
                    dto.setClassNum(course.getClassNum());
                    dto.setName(course.getName());
                    dto.setPoint(course.getPoint());
                    dto.setType(String.valueOf(course.getType()));
                    dto.setTime(course.getTime());
                    dto.setClassroom(course.getClassroom());
                    dto.setCapacity(course.getCapacity());
                    dto.setCategory(course.getCategory());

                    // 获取教师名称
                    String teacherName = userMapper.getUsernameById(course.getTeacherId());
                    dto.setTeacherName(teacherName);

                    // 获取已选人数
                    Integer selectedCount = classMapper.countCourseByCourseId(course.getId());
                    dto.setSelectedCount(selectedCount != null ? selectedCount : 0);

                    resultList.add(dto);
                }
            }

            return ResponseUtil.build(Result.success(resultList, "查询成功"));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("查询失败：" + e.getMessage(), e);
        }
    }

}
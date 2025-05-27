package com.orbithy.cms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.orbithy.cms.data.dto.*;
import com.orbithy.cms.data.po.ClassCourse;
import com.orbithy.cms.data.po.Classes;
import com.orbithy.cms.data.po.Grade;
import com.orbithy.cms.data.po.User;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.exception.CustomException;
import com.orbithy.cms.mapper.*;
import com.orbithy.cms.utils.ResponseUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClassService {
    @Autowired
    private ClassMapper classMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ClassCourseMapper classCourseMapper;
    @Autowired
    private GradeMapper gradeMapper;
    @Autowired
    private CourseSelectionMapper courseSelectionMapper;

    // 定义每天的时间段范围
    private static final int SLOTS_PER_DAY = 5;  // 每天5个时间段
    private static final int DAYS = 5;           // 5个工作日

    /**
     * 教师创建课程
     */
    public ResponseEntity<Result> createCourse(String id, CreateCourseDTO courseDTO) {
        try {

            // 创建课程对象
            Classes course = new Classes();
            BeanUtils.copyProperties(courseDTO, course);
            course.setTeacherId(Integer.parseInt(id));
            course.setStatus(Classes.CourseStatus.待审批);

            // 转换课程类型
            try {
                course.setType(Classes.CourseType.valueOf(courseDTO.getType()));
            } catch (IllegalArgumentException e) {
                throw new CustomException("无效的课程类型", e);
            }

            // 验证课程数据
            if (!isValidCourseData(course)) {
                throw new CustomException("课程信息不合法");
            }

            // 保存课程
            classMapper.createCourse(course);
            return ResponseUtil.build(Result.ok());
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("创建课程失败：" + e.getMessage(), e);
        }
    }

    /**
     * 验证课程数据
     */
    private boolean isValidCourseData(Classes course) {
        // 验证课时
        if (course.getPeriod() <= 0 || course.getPeriod() > 120) {
            return false;
        }

        // 验证周数
        if (course.getWeekStart() < 1 || course.getWeekEnd() > 17 ||
                course.getWeekStart() > course.getWeekEnd()) {
            return false;
        }

        // 验证容量
        if (course.getCapacity() <= 0 || course.getCapacity() > 200) {
            return false;
        }

        // 验证学期格式
        if (!course.getTerm().matches("\\d{4}-\\d{4}-[12]")) {
            return false;
        }

        // 验证成绩比例
        if (course.getRegularRatio().add(course.getFinalRatio())
                .compareTo(new BigDecimal("1.00")) != 0) {
            return false;
        }

        return true;
    }

    /**
     * 教务审批课程
     */
    public ResponseEntity<Result> approveCourse(String id, Integer courseId, Integer status, String classNum, String reason, Integer ccourseId) {
        try {
            // 验证状态值
            if (status != 1 && status != 2) {
                throw new CustomException("无效的审批状态");
            }

            Classes course = classMapper.getCourseById(courseId);
            if (course == null) {
                throw new CustomException("课程不存在");
            }

            // 验证课程状态
            if (course.getStatus() != Classes.CourseStatus.待审批) {
                throw new CustomException("只能审批待审批的课程");
            }

            // 如果审批通过
            if (status == 1) {
                // 获取当前课程的课序号
                String existingClassNum = course.getClassNum();

                // 如果课程没有课序号，且审批时也没提供课序号
                if ((existingClassNum == null || existingClassNum.trim().isEmpty())
                        && (classNum == null || classNum.trim().isEmpty())) {
                    throw new CustomException("课程没有课序号，审批通过时必须提供课序号");
                }

                // 如果提供了新的课序号，使用新课序号；否则保留原课序号
                String finalClassNum = (classNum != null && !classNum.trim().isEmpty()) ? classNum : existingClassNum;

                // 如果课序号发生变化，检查唯一性（应该是不需要的）
//                if (!finalClassNum.equals(existingClassNum)) {
//                    // 验证课序号唯一性
//                    QueryWrapper<Classes> queryWrapper = new QueryWrapper<>();
//                    queryWrapper.eq("class_num", finalClassNum)
//                            .eq("term", course.getTerm());
//                    if (classMapper.selectCount(queryWrapper) > 0) {
//                        throw new CustomException("该学期已存在相同课序号");
//                    }
//                }
                if (course.getType() == Classes.CourseType.必修 && ccourseId == null) {
                    throw new CustomException("必修课需要和班级绑定，审批通过时必须提供班级");
                }else if(ccourseId != null){classCourseMapper.insertClassCourse(ccourseId, courseId);

                }

                // 更新课程状态和课序号（如果有变化）
                classMapper.updateCourseStatusAndClassNum(courseId, status, finalClassNum, null);
            } else {
                // 拒绝时更新状态和拒绝理由
                classMapper.refuseClass(courseId, status, reason);
            }

            // 如果拒绝，验证拒绝理由
            if (status == 2 && (reason == null || reason.trim().isEmpty())) {
                throw new CustomException("拒绝时必须提供拒绝理由");
            }

            String message = status == 1 ? "课程审批通过" : "课程审批拒绝";
            return ResponseUtil.build(Result.success(null, message));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("审批失败：" + e.getMessage(), e);
        }
    }

    /**
     * 获取课程列表
     */
    public ResponseEntity<Result> getCourseList(String id, String term, Integer pageNum, Integer pageSize) {
        try {
            if (term != null && !term.matches("\\d{4}-\\d{4}-[12]")) {
                throw new CustomException("无效的学期格式");
            }
            int permission = userMapper.getPermission(id);
            List<ClassListDTO> classList;
            int total;
            int offset = (pageNum - 1) * pageSize;

            switch (permission) {
                case 0: // 教务
                    if (term != null) {
                        classList = classMapper.getCoursesByTermByPage(term, offset, pageSize);
                        total = classMapper.countCoursesByTerm(term);
                    } else {
                        classList = classMapper.getAllCoursesByPage(offset, pageSize);
                        total = classMapper.countAllCourses();
                    }
                    break;
                case 1: // 教师
                    if (term != null) {
                        classList = classMapper.getTeacherCoursesByTermByPage(Integer.parseInt(id), term, offset, pageSize);
                        total = classMapper.countTeacherCoursesByTerm(Integer.parseInt(id), term);
                    } else {
                        classList = classMapper.getTeacherCoursesByPage(Integer.parseInt(id), offset, pageSize);
                        total = classMapper.countTeacherCourses(Integer.parseInt(id));
                    }
                    break;
                default:
                    throw new CustomException("无效的用户权限");
            }

            return getResultResponseEntity(pageNum, pageSize, classList, total);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("获取课程列表失败：" + e.getMessage(), e);
        }
    }

    /**
     * 获取课程详情
     */
    public ResponseEntity<Result> getCourseDetail(String id, Integer courseId) {
        try {
            ClassDTO course = classMapper.getCourseDeById(courseId);
            if (course == null) {
                throw new CustomException("课程不存在");
            }
            course.setTeacherName(userMapper.getUsernameById(course.getTeacherId()));

            // 验证权限
            int permission = userMapper.getPermission(id);
            if (permission != 0 && // 教务
                    !course.getTeacherId().toString().equals(id)) { // 课程创建者
                throw new CustomException("无权限查看此课程");
            }

            return ResponseUtil.build(Result.success(course, "获取成功"));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("获取课程详情失败：" + e.getMessage(), e);
        }
    }

    /**
     * 更新课程
     */
    public ResponseEntity<Result> updateCourse(String id, Integer courseId, CreateCourseDTO courseDTO) {
        try {
            // 验证教师权限和所有权
            Classes course = classMapper.getCourseById(courseId);
            if (course == null) {
                throw new CustomException("课程不存在");
            }
            if (!course.getTeacherId().toString().equals(id)) {
                throw new CustomException("无权限修改此课程");
            }

            // 验证课程状态
            if (course.getStatus() == Classes.CourseStatus.已通过) {
                throw new CustomException("只能修改待审批的课程");
            }

            // 更新课程信息
            BeanUtils.copyProperties(courseDTO, course);
            try {
                course.setType(Classes.CourseType.valueOf(courseDTO.getType()));
            } catch (IllegalArgumentException e) {
                throw new CustomException("无效的课程类型", e);
            }

            // 验证课程数据
            if (!isValidCourseData(course)) {
                throw new CustomException("课程信息不合法");
            }

            classMapper.updateById(course);
            return ResponseUtil.build(Result.success(null, "更新成功"));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("更新课程失败：" + e.getMessage(), e);
        }
    }

    /**
     * 删除课程
     */
    public ResponseEntity<Result> deleteCourse(String id, Integer courseId) {
        try {
            Classes course = classMapper.getCourseById(courseId);
            if (course == null) {
                throw new CustomException("课程不存在");
            }
            if (!course.getTeacherId().toString().equals(id)) {
                throw new CustomException("无权限删除此课程");
            }

            // 验证课程状态
            if (course.getStatus() != Classes.CourseStatus.待审批) {
                throw new CustomException("只能删除待审批的课程");
            }

            classMapper.deleteById(courseId);
            return ResponseUtil.build(Result.success(null, "删除成功"));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("删除课程失败：" + e.getMessage(), e);
        }
    }

    /**
     * 获取待批准的课程列表
     */
    public ResponseEntity<Result> getPendingCourses() {
        try {
            List<ClassDTO> pendingCourses = classMapper.getPendingCourses();
            for (ClassDTO classDTO : pendingCourses) {
                String teacherName = userMapper.getUsernameById(classDTO.getTeacherId());
                classDTO.setTeacherName(teacherName);
            }
            return ResponseUtil.build(Result.success(pendingCourses, "获取待批准课程成功"));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("获取待批准课程失败：" + e.getMessage(), e);
        }
    }

    /**
     * 自动排课
     */
    public ResponseEntity<Result> autoSchedule(String id, String term) {
        try {


            // 验证学期格式
            if (term == null || !term.matches("\\d{4}-\\d{4}-[12]")) {
                throw new CustomException("无效的学期格式");
            }

            // 获取需要排课的课程（状态为已通过审批的课程）
            List<Classes> courses = classMapper.getCoursesByTermAndStatus(term, 1);
            if (courses.isEmpty()) {
                throw new CustomException("没有需要排课的课程");
            }

            // 执行排课
            boolean success = generateSchedule(courses);

            if (!success) {
                throw new CustomException("无法找到合适的排课方案，请检查课程时间冲突或教室容量限制");
            }

            // 生成排课结果报告
            StringBuilder report = new StringBuilder("排课成功！\n");
            for (Classes course : courses) {
                report.append(String.format("课程：%s\n", course.getName()));
                report.append(String.format("教师：%s\n", userMapper.getUsernameById(course.getTeacherId())));
                report.append("上课时间：\n");
                Arrays.stream(course.getTime().split(","))
                        .map(Integer::parseInt)
                        .forEach(slot -> report.append(getTimeSlotInfo(slot)).append("\n"));
                report.append("\n");
            }

            return ResponseUtil.build(Result.success(report.toString(), "自动排课完成"));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("自动排课失败：" + e.getMessage(), e);
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
        // 创建教室容量表 [天数][时间段]
        int[][] classroomCapacity = new int[DAYS][SLOTS_PER_DAY];
        // 设置每个时间段的最大教室容量
//        int MAX_CLASSROOM_CAPACITY = 200;

        for (Classes course : courses) {
            boolean scheduled = false;
            int teacherId = course.getTeacherId();
            int courseCapacity = course.getCapacity();

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
                                !assignedSlots.contains(actualSlot) ) {

                            // 分配时间段
                            timeSlotOccupied[day][slot] = true;
                            teacherSchedule.get(teacherId)[day][slot] = true;
                            classroomCapacity[day][slot] += courseCapacity;
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
     * 教务删除审批过的课程
     */
    public ResponseEntity<Result> adminDeleteCourse(String id, Integer courseId) {
        try {


            // 获取课程信息
            Classes course = classMapper.getCourseById(courseId);
            if (course == null) {
                throw new CustomException("课程不存在");
            }

            // 验证课程状态
            if (course.getStatus() != Classes.CourseStatus.已通过) {
                throw new CustomException("只能删除已审批通过的课程");
            }

            // 删除课程
            classMapper.deleteById(courseId);
            return ResponseUtil.build(Result.success(null, "删除成功"));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("删除课程失败：" + e.getMessage(), e);
        }
    }

    public ResponseEntity<Result> getSelectedStudents(int permission, String id, Integer courseId) {
        // 获取课程信息
        if (permission != 0) {
            Integer TeacherId = classMapper.getTeacherIdByCourseId(courseId);
            if (TeacherId == null) {
                return ResponseUtil.build(Result.error(404, "课程不存在"));
            }
            if (!TeacherId.toString().equals(id)) {
                return ResponseUtil.build(Result.error(403, "无权限查看选课学生"));
            }
        }

        // 获取选课学生列表
        List<String> studentIds = classMapper.getSelectedStudents(courseId);
        List<StudentSectionDTO> result = studentIds.isEmpty()
                ? Collections.emptyList()
                : classMapper.getStudentSectionInfo(studentIds, courseId);

        return ResponseUtil.build(Result.success(result, "获取选课学生成功"));

    }

    public ResponseEntity<Result> getReason(String id, Integer courseId) {
        if (!classMapper.getTeacherIdByCourseId(courseId).toString().equals(id) && userMapper.getPermission(id) != 0) {
            return ResponseUtil.build(Result.error(403, "无权限"));
        }
        return ResponseUtil.build(Result.success(classMapper.getRefuseReasonByCourseId(courseId), "获取成功"));
    }

    public ResponseEntity<Result> getClassSchedule(String id, Integer week, String term) {
        if(userMapper.getPermission(id) != 1 && userMapper.getPermission(id) != 2){
            return ResponseUtil.build(Result.error(403, "无权限"));
        }
            
        int permission = userMapper.getPermission(id);
        List<ClassDTO> classSchedule = Collections.emptyList();
        if (permission == 1) {
            classSchedule = classMapper.getClassScheduleTea(id,term, week);
        } else if (permission == 2) {
            classSchedule = classMapper.getClassScheduleSdu(id,term,week);

        }
        return ResponseUtil.build(Result.success(classSchedule,"获取成功"));

    }

    public ResponseEntity<Result> getTeacherCourses(String id, String term, Integer pageNum, Integer pageSize, String keyword) {
        int offset = (pageNum - 1) * pageSize;
        List<ClassListDTO> classList = classMapper.searchTeacherCourses(Integer.parseInt(id), term, offset, pageSize, keyword);
        int total = classMapper.countTeacherCoursesByTerm(Integer.parseInt(id), term);

        return getResultResponseEntity(pageNum, pageSize, classList, total);
    }

    @NotNull
    private ResponseEntity<Result> getResultResponseEntity(Integer pageNum, Integer pageSize, List<ClassListDTO> classList, int total) {
        for (ClassListDTO classListDTO : classList) {
            String teacherName = userMapper.getUsernameById(classMapper.getTeacherIdByCourseId(classListDTO.getId()));
            classListDTO.setTeacherName(teacherName);
            Integer cla = classListDTO.getId();
            Integer num = classMapper.countCourseByCourseId(cla);
            num = num == null ? 0 : num;
            classListDTO.setPeopleNum(num);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", classList);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        int pages = (int) Math.ceil((double) total / pageSize);
        result.put("pages", pages);

        return ResponseUtil.build(Result.success(result, "获取课程列表成功"));
    }

    public ResponseEntity<Result> adUpdate(String id, Integer classId, ChangeClassDTO changeClassDTO){
        try {

            Classes course = classMapper.getCourseById(classId);
            ClassCourse classCourse = new ClassCourse();
            classCourse.setClassId(classId);
            classCourse.setCourseId(course.getId());
            if (course == null) {
                throw new CustomException("课程不存在");
            }

            // 更新课程信息
            BeanUtils.copyProperties(changeClassDTO, course);
            try {
                course.setType(Classes.CourseType.valueOf(String.valueOf(changeClassDTO.getType())));
            } catch (IllegalArgumentException e) {
                throw new CustomException("无效的课程类型", e);
            }

            // 验证课程数据
            if (!isValidCourseData(course)) {
                throw new CustomException("课程信息不合法");
            }

            classMapper.updateById(course);
            classCourseMapper.updateById(classCourse);
            return ResponseUtil.build(Result.success(null, "更新成功"));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("更新课程失败：" + e.getMessage(), e);
        }
    }

    public ResponseEntity<Result> updateRank(Integer classId) {
        QueryWrapper<Grade> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("class_id", classId)  // classId 等于特定值
                .orderByDesc("grade");
        List<Grade> students = gradeMapper.selectList(queryWrapper);
        for (int i = 0; i < students.size(); i++) {
            students.get(i).setRank((byte)(i + 1));
            gradeMapper.updateById(students.get(i));
        }
        return ResponseUtil.build(Result.success(null, "更新成功"));


    }

    public ResponseEntity<Result> updatePointRank( int grade) {
        String str = grade + "%";
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("permission",2);
        queryWrapper.like( "SDUId",str );
        List<User> users = userMapper.selectList(queryWrapper);
        List<UserDTO> userDTOs = users.stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
        for (UserDTO user : userDTOs) {
            int totalPoint = courseSelectionMapper.sumAllPointById(user.getSDUId(),"all");
            int averCredits;
            if (totalPoint == 0){
                averCredits = 0;
            } else  {
                averCredits = gradeMapper.getTotalGrade(user.getSDUId(),"all") / totalPoint;
            }

            user.setProcessed(averCredits);
        }
        userDTOs.sort((u1, u2) -> Integer.compare(u2.getProcessed(), u1.getProcessed()));

        return ResponseUtil.build(Result.success(null, "更新成功"));
    }
}

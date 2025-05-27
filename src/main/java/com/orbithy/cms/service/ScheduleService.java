package com.orbithy.cms.service;

import com.orbithy.cms.data.po.Classes;
import com.orbithy.cms.data.po.Rooms;

import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.domain.Course;
import com.orbithy.cms.domain.Room;
import com.orbithy.cms.domain.Teacher;
import com.orbithy.cms.domain.TimeTable;

import com.orbithy.cms.exception.CustomException;
import com.orbithy.cms.mapper.ClassCourseMapper;
import com.orbithy.cms.mapper.ClassMapper;
import com.orbithy.cms.mapper.RoomMapper;

import com.orbithy.cms.utils.ResponseUtil;
import jakarta.annotation.Resource;
import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ScheduleService {

    @Resource
    private ClassMapper classesMapper;

    @Resource
    private RoomMapper roomMapper;

    @Resource
    private ClassCourseMapper classCourseMapper;

    @Resource
    private SolverManager<TimeTable, UUID> solverManager;

    // category → roomId 映射
    private static final Map<String, Integer> SPORTS_ROOM_BINDING = Map.of(
            "羽毛球", 37,
            "篮球", 41,
            "网球", 40,
            "乒乓球", 38,
            "武术", 39,
            "健美操", 42
    );

    private String getRoomLocationById(Integer id, List<Rooms> roomList) {
        return roomList.stream()
                .filter(r -> r.getId().equals(id))
                .map(Rooms::getLocation)
                .findFirst()
                .orElse("未知教室");
    }

    public ResponseEntity<Result> schedule(String term) {
        // 验证学期格式
        if (term == null || !term.matches("\\d{4}-\\d{4}-[12]")) {
            throw new CustomException("无效的学期格式");
        }

        // 从数据库获取课程
        List<Classes> classesFromDb = classesMapper.getActiveCoursesByTerm(term);

        // 从数据库获取教室
        List<Rooms> roomsFromDb = roomMapper.selectList(null);

        // 将 po.Rooms 转换为 domain.Room
        List<Room> roomList = roomsFromDb.stream()
                .map(r -> new Room(r.getId(), r.getLocation()))
                .collect(Collectors.toList());

        // 构造可用时间段（0~24）
        List<Integer> timeSlotList = IntStream.rangeClosed(0, 24).boxed().toList();

        // 将 po.Classes 转换为 domain.Course
        List<Course> courseList = classesFromDb.stream()
                .map(c -> {
                    Course course = new Course();
                    course.setId(c.getId());
                    course.setName(c.getName());
                    course.setWeekStart(c.getWeekStart());
                    course.setWeekEnd(c.getWeekEnd());
                    course.setType(c.getType());

                    course.setClassId(classCourseMapper.selectClassIdByCourseId(c.getId()));
                    Teacher t = new Teacher(c.getTeacherId());
                    course.setTeacher(t);

                    if ("体育".equals(c.getName()) && SPORTS_ROOM_BINDING.containsKey(c.getCategory())) {
                        course.setFixedRoom(true);

                        Integer fixedRoomId = SPORTS_ROOM_BINDING.get(c.getCategory());
                        course.setRequiredRoomId(fixedRoomId);
                        course.setRoom(new Room(fixedRoomId, getRoomLocationById(fixedRoomId, roomsFromDb)));
                    } else {
                        course.setFixedRoom(false);
                    }

                    return course;
                }).collect(Collectors.toList());

        // 构建排课问题对象（domain.TimeTable）
        TimeTable problem = new TimeTable();
        problem.setCourseList(courseList);
        problem.setRoomList(roomList);
        problem.setTimeSlotIdList(timeSlotList);

        // 提交给 OptaPlanner 求解
        UUID problemId = UUID.randomUUID();
        solverManager.solveAndListen(
                problemId,
                id -> problem,   // 初始模型
                this::saveSolution     // 解完后保存
        );
        return ResponseUtil.build(Result.ok());
    }

    private void saveSolution(TimeTable solution) {
        // 把排完课后的结果保存回数据库
        for (Course course : solution.getCourseList()) {
            classesMapper.updateCourseSchedule(
                    course.getId(),
                    String.valueOf(course.getTimeSlotId()),
                    course.getRoom().getId()
            );
        }
    }
}

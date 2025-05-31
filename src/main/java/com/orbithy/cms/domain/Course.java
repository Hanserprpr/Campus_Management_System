package com.orbithy.cms.domain;

import com.orbithy.cms.domain.Teacher;
import lombok.Data;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import com.orbithy.cms.data.enums.CourseType;

import java.util.List;

@Data
@PlanningEntity
public class Course {

    @PlanningId
    private Integer id;
    private String name;
    private String classNum;
    private List<Integer> classId; // 班级ID
    private CourseType type; // 必修、限选、任选
    private Teacher teacher;
    private int weekStart;
    private int weekEnd;

    @PlanningVariable(valueRangeProviderRefs = {"timeslotRange"})
    private Integer timeSlotId;  // 一周内的节次：0~24

    @PlanningVariable(valueRangeProviderRefs = {"roomRange"})
    private Room room;

    private boolean fixedRoom;        // 是否绑定固定教室
    private Integer requiredRoomId;
}

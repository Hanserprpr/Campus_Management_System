package com.orbithy.cms.domain;

import lombok.Data;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

@Data
@PlanningSolution
public class TimeTable {

    @PlanningScore
    private HardSoftScore score;


    @PlanningEntityCollectionProperty
    private List<Course> courseList;

    @ValueRangeProvider(id = "roomRange")
    private List<Room> roomList;

    @ValueRangeProvider(id = "timeslotRange")
    private List<Integer> timeSlotIdList; // 0 ~ 24

}



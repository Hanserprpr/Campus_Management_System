package com.orbithy.cms.solver;

import com.orbithy.cms.domain.Course;
import com.orbithy.cms.data.enums.CourseType;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class TimeTableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[]{
                requiredCourseConflict(factory),
                roomConflict(factory),
                teacherConflict(factory)
        };
    }

    private Constraint requiredCourseConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(Course.class,
                        Joiners.equal(Course::getTimeSlotId),
                        Joiners.equal(Course::getClassId),
                        Joiners.filtering((c1, c2) ->
                                c1.getType() == CourseType.必修 &&
                                        c2.getType() == CourseType.必修 &&
                                        weeksOverlap(c1, c2)
                        )
                ).penalize(HardSoftScore.ONE_HARD)
                .asConstraint("必修课冲突");
    }

    private Constraint roomConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(Course.class,
                        Joiners.equal(Course::getTimeSlotId),
                        Joiners.equal(Course::getRoom),
                        Joiners.filtering(TimeTableConstraintProvider::weeksOverlap)
                ).penalize(HardSoftScore.ONE_HARD)
                .asConstraint("教室冲突");
    }

    private Constraint teacherConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(Course.class,
                        Joiners.equal(Course::getTimeSlotId),
                        Joiners.equal(Course::getTeacher),
                        Joiners.filtering(TimeTableConstraintProvider::weeksOverlap)
                ).penalize(HardSoftScore.ONE_HARD)
                .asConstraint("教师冲突");
    }

    private Constraint fixedRoomEnforced(ConstraintFactory factory) {
        return factory.forEach(Course.class)
                .filter(course -> course.isFixedRoom() &&
                        (course.getRoom() == null || !course.getRoom().getId().equals(course.getRequiredRoomId())))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("体育课必须绑定指定教室");
    }


    private static boolean weeksOverlap(Course c1, Course c2) {
        return !(c1.getWeekEnd() < c2.getWeekStart() || c2.getWeekEnd() < c1.getWeekStart());
    }
}
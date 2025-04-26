package com.orbithy.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.cms.data.po.CourseSelection;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CourseSelectionMapper extends BaseMapper<CourseSelection> {
    
    @Select("SELECT * FROM course_reg  WHERE student_id = #{studentId}")
    List<CourseSelection> getStudentSelections(@Param("studentId") Integer studentId);

    @Select("SELECT COUNT(*) FROM course_reg WHERE course_id = #{courseId}")
    int getCourseSelectionCount(@Param("courseId") Integer courseId);

    @Select("SELECT * FROM course_reg WHERE student_id = #{studentId} AND course_id = #{courseId}")
    CourseSelection getSelection(@Param("studentId") Integer studentId, @Param("courseId") Integer courseId);

    @Select("SELECT SUM(c.point) FROM course_reg cr " +
            "JOIN classes c ON cr.course_id = c.id " +
            "WHERE cr.student_id = #{studentId}")
    Integer getTotalPoints(@Param("studentId") Integer studentId);
} 
package com.orbithy.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.cms.data.po.Grade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GradeMapper extends BaseMapper<Grade> {
    @Select("SELECT student_id, course_id, grade, rank FROM grade WHERE course_id = #{courseId}")
    List<Grade> getGradeByCourseId(int courseId);

    @Update("UPDATE classes SET published = true WHERE id = #{courseId}")
    void releaseGrade(int courseId);
}

package com.orbithy.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.cms.data.dto.GradeTermDTO;
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

    @Select("SELECT g.id, g.course_id, g.regular, g.final, g.grade, g.rank, c.class_num, c.point, c.teacher_id, c.type, u.username " +
            "FROM grade g " +
            "JOIN classes c ON g.course_id = c.id " +
            "JOIN user u ON c.teacher_id = u.id "+
            "WHERE student_id = #{id} AND g.term = #{term}")
    List<GradeTermDTO> getGradeByTerm(String id, String term);
}

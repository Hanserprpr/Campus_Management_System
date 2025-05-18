package com.orbithy.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.cms.data.dto.GradeTermDTO;
import com.orbithy.cms.data.po.Grade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GradeMapper extends BaseMapper<Grade> {
    @Select("SELECT student_id, course_id, regular, final_score, grade, `rank` FROM grade WHERE course_id = #{courseId}")
    List<Grade> getGradeByCourseId(int courseId);

    @Update("UPDATE classes SET published = true WHERE id = #{courseId}")
    void releaseGrade(int courseId);

    @Select("SELECT g.id, g.course_id, g.regular, g.final_score, g.grade, g.rank, c.class_num, c.point, c.teacher_id, c.type, u.username AS teacher " +
            "FROM grade g " +
            "JOIN classes c ON g.course_id = c.id " +
            "JOIN user u ON c.teacher_id = u.id "+
            "WHERE student_id = #{id} AND g.term = #{term}")
    List<GradeTermDTO> getGradeByTerm(String id, String term);

    @Update("""
                UPDATE grade t1
                JOIN (
                    SELECT id, RANK() OVER (PARTITION BY course_id ORDER BY grade DESC) AS r
                    FROM grade
                    WHERE course_id = #{courseId}
                ) t2 ON t1.id = t2.id
                SET t1.rank = t2.r
                WHERE t1.course_id = #{courseId}
            """)
    void updateRankByCourse(@Param("courseId") Integer courseId);

    @Select("SELECT COUNT(*) FROM grade WHERE course_id = #{courseId} AND student_id = #{studentId}")
    boolean getGradeByCourseIdAndStudentId(Integer courseId, Integer studentId);
}

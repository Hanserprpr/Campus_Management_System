package com.orbithy.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.cms.data.dto.ClassesDTO;
import com.orbithy.cms.data.po.Classes;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ClassMapper extends BaseMapper<Classes> {
    @Insert("INSERT INTO classes (name, category, point, teacher_id, classroom, week_start, week_end, " +
            "period, college, term, class_num, type, capacity, status, intro, examination, " +
            "regular_ratio, final_ratio) " +
            "VALUES (#{name}, #{category}, #{point}, #{teacherId}, #{classroom}, #{weekStart}, #{weekEnd}, " +
            "#{period}, #{college}, #{term}, #{classNum}, #{type}, #{capacity}, #{status.code}, #{intro}, " +
            "#{examination}, #{regularRatio}, #{finalRatio})")
    void createCourse(Classes course);

    @Update("UPDATE classes SET status = #{status}, class_num = #{classNum}, f_reason = #{reason} WHERE id = #{courseId}")
    int updateCourseStatusAndClassNum(@Param("courseId") Integer courseId, @Param("status") Integer status, @Param("classNum") String classNum, @Param("reason") String reason);

    @Select("SELECT * FROM classes")
    List<Classes> getAllClasses();

    @Select("SELECT * FROM classes WHERE id = #{courseId}")
    Classes getCourseById(@Param("courseId") Integer courseId);

    @Select("SELECT id, class_num, name FROM classes WHERE teacher_id = #{teacherId}")
    List<Classes> getTeacherCourses(@Param("teacherId") Integer teacherId);

    @Select("SELECT id, class_num, name FROM classes WHERE term = #{term}")
    List<Classes> getCoursesByTerm(@Param("term") String term);

    @Select("SELECT id, class_num, name FROM classes WHERE teacher_id = #{teacherId} AND term = #{term}")
    List<Classes> getTeacherCoursesByTerm(@Param("teacherId") Integer teacherId, @Param("term") String term);

    @Select("SELECT * FROM classes WHERE (class_num LIKE CONCAT('%', #{keyword}, '%') " +
            "OR name LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND status = #{status.code}")
    List<Classes> searchCourses(@Param("keyword") String keyword);

    @Select("SELECT * FROM classes WHERE status = 0")
    List<Classes> getPendingCourses();

    @Select("SELECT teacher_id FROM classes WhERE id = #{courseId}")
    Integer getTeacherIdByCourseId(@Param("courseId") String courseId);

    @Update("UPDATE classes SET time = #{time} WHERE id = #{courseId}")
    void updateCourseTime(@Param("courseId") Integer courseId, @Param("time") String time);

    @Select("SELECT * FROM classes WHERE term = #{term} AND status = #{status}")
    List<Classes> getCoursesByTermAndStatus(@Param("term") String term, @Param("status") Integer status);
}
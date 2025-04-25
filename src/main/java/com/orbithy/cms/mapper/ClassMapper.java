package com.orbithy.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.orbithy.cms.data.dto.ClassDTO;
import com.orbithy.cms.data.dto.ClassListDTO;
import com.orbithy.cms.data.dto.StudentSectionDTO;
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

    @Update("UPDATE classes SET status = #{status}, f_reason = #{reason} WHERE id = #{courseId}")
    void refuseClass(@Param("courseId") Integer courseId, @Param("status") Integer status, @Param("reason") String reason);

    @Select("SELECT * FROM classes WHERE status = 1")
    List<Classes> getAllClasses();

    @Select("SELECT * FROM classes WHERE id = #{courseId}")
    Classes getCourseById(@Param("courseId") Integer courseId);

    @Select("SELECT id, class_num, name, point,term, status FROM classes WHERE teacher_id = #{teacherId}")
    List<ClassListDTO> getTeacherCourses(@Param("teacherId") Integer teacherId);

    @Select("SELECT id, class_num, name, point, term, status FROM classes WHERE term = #{term}")
    List<ClassListDTO> getCoursesByTerm(@Param("term") String term);

    @Select("SELECT id, class_num, name, point, term,status FROM classes WHERE teacher_id = #{teacherId} AND term = #{term}")
    List<ClassListDTO> getTeacherCoursesByTerm(@Param("teacherId") Integer teacherId, @Param("term") String term);

    @Select("SELECT * FROM classes WHERE (class_num LIKE CONCAT('%', #{keyword}, '%') " +
            "OR name LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND status = 1")
    List<Classes> searchCourses(@Param("keyword") String keyword);

    @Select("SELECT * FROM classes WHERE status = 0")
    List<ClassDTO> getPendingCourses();

    @Select("SELECT teacher_id FROM classes WhERE id = #{courseId}")
    Integer getTeacherIdByCourseId(@Param("courseId") String courseId);

    @Update("UPDATE classes SET time = #{time} WHERE id = #{courseId}")
    void updateCourseTime(@Param("courseId") Integer courseId, @Param("time") String time);

    @Select("SELECT * FROM classes WHERE term = #{term} AND status = #{status}")
    List<Classes> getCoursesByTermAndStatus(@Param("term") String term, @Param("status") Integer status);

    @Select("SELECT id, class_num, name, point, term, status FROM classes")
    List<ClassListDTO> select();

    @Select("SELECT COUNT(*) FROM course_reg WHERE course_id = #{courseId}")
    Integer countCourseByCourseId(@Param("courseId") Integer courseId);

    @Select("SELECT * FROM classes WHERE id = #{courseId}")
    ClassDTO getCourseDeById(@Param("courseId") Integer courseId);

    @Select("SELECT teacher_id FROM classes WHERE id = #{courseId}")
    Integer getTeacherIdByCourseId(@Param("courseId") Integer courseId);

    @Select("SELECT student_id FROM course_reg WHERE course_id = #{courseId}")
    List<String> getSelectedStudents(Integer courseId);


    List<StudentSectionDTO> getStudentSectionInfo(@Param("ids") List<String> ids);

    @Select("SELECT * FROM classes WHERE term = #{term}")
    List<Classes> getCourseByTerm(String term);
}
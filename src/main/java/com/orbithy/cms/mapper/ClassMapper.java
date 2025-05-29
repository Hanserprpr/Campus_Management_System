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
    @Insert("INSERT INTO classes (name, category, point, teacher_id, classroom_id, week_start, week_end, " +
            "period, college, term, class_num, type, capacity, status, intro, examination, " +
            "regular_ratio, final_ratio) " +
            "VALUES (#{name}, #{category}, #{point}, #{teacherId}, #{classroomId}, #{weekStart}, #{weekEnd}, " +
            "#{period}, #{college}, #{term}, #{classNum}, #{type}, #{capacity}, #{status.code}, #{intro}, " +
            "#{examination}, #{regularRatio}, #{finalRatio})")
    void createCourse(Classes course);

    @Update("UPDATE classes SET status = #{status}, class_num = #{classNum}, f_reason = #{reason} WHERE id = #{courseId}")
    int updateCourseStatusAndClassNum(@Param("courseId") Integer courseId, @Param("status") Integer status, @Param("classNum") String classNum, @Param("reason") String reason);

    @Update("UPDATE classes SET status = #{status}, f_reason = #{reason} WHERE id = #{courseId}")
    void refuseClass(@Param("courseId") Integer courseId, @Param("status") Integer status, @Param("reason") String reason);



    @Select("SELECT * FROM classes WHERE id = #{courseId}")
    Classes getCourseById(@Param("courseId") Integer courseId);


    List<Classes> searchCourses(@Param("studentId") Integer studentId, @Param("keyword") String keyword, String term, String type);

    @Select("SELECT * FROM classes WHERE status = 0")
    List<ClassDTO> getPendingCourses();

    @Update("UPDATE classes SET time = #{time} WHERE id = #{courseId}")
    void updateCourseTime(@Param("courseId") Integer courseId, @Param("time") String time);

    @Select("SELECT * FROM classes WHERE term = #{term} AND status = #{status}")
    List<Classes> getCoursesByTermAndStatus(@Param("term") String term, @Param("status") Integer status);

    @Select("SELECT id, class_num, name, point, term, status, regular_ratio, final_ratio FROM classes WHERE status = 1")
    List<ClassListDTO> select();

    @Select("SELECT COUNT(*) FROM course_reg WHERE course_id = #{courseId}")
    Integer countCourseByCourseId(Integer courseId);

    @Select("SELECT * FROM classes WHERE id = #{courseId}")
    ClassDTO getCourseDeById(Integer courseId);

    @Select("SELECT teacher_id FROM classes WHERE id = #{courseId, javaType=java.lang.Integer}")
    Integer getTeacherIdByCourseId(Integer courseId);

    @Select("SELECT student_id FROM course_reg WHERE course_id = #{courseId}")
    List<String> getSelectedStudents(Integer courseId);


    List<StudentSectionDTO> getStudentSectionInfo(@Param("ids") List<String> ids, int courseId);

    @Select("SELECT * FROM classes WHERE term = #{term} AND status = 1")
    List<Classes> getCourseByTerm(String term);

    @Select("SELECT f_reason FROM classes WHERE id = #{courseId}")
    String getRefuseReasonByCourseId(Integer courseId);


    @Select("SELECT id, class_num, name, point, term, status, regular_ratio, final_ratio, type, college FROM classes WHERE term = #{term} AND status IN (1, 3) LIMIT #{offset}, #{size}")
    List<ClassListDTO> getCoursesByTermByPage(@Param("term") String term, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM classes WHERE term = #{term}")
    int countCoursesByTerm(@Param("term") String term);

    @Select("SELECT id, class_num, name, point, term, status, regular_ratio, final_ratio, type, college FROM classes WHERE status IN (1, 3) LIMIT #{offset}, #{size}")
    List<ClassListDTO> getAllCoursesByPage(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM classes")
    int countAllCourses();

    @Select("SELECT id, class_num, name, point, term, status, regular_ratio, final_ratio, type FROM classes WHERE teacher_id = #{teacherId} LIMIT #{offset}, #{size}")
    List<ClassListDTO> getTeacherCoursesByPage(@Param("teacherId") Integer teacherId, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM classes WHERE teacher_id = #{teacherId}")
    int countTeacherCourses(@Param("teacherId") Integer teacherId);

    @Select("SELECT id, class_num, name, point, term, status, regular_ratio, final_ratio, type FROM classes WHERE teacher_id = #{teacherId} AND term = #{term} LIMIT #{offset}, #{size}")
    List<ClassListDTO> getTeacherCoursesByTermByPage(@Param("teacherId") Integer teacherId, @Param("term") String term, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM classes WHERE teacher_id = #{teacherId} AND term = #{term}")
    int countTeacherCoursesByTerm(@Param("teacherId") Integer teacherId, @Param("term") String term);

    @Select("SELECT id, class_num, name, time, classroom_id FROM classes WHERE teacher_id = #{teacherId} AND term = #{term} AND week_start <= #{week} AND week_end >= #{week} AND status = 1")
    List<ClassDTO>getClassScheduleTea(@Param("teacherId") String teacherId, @Param("term") String term, @Param("week") Integer week);

    @Select("SELECT cl.id, cl.class_num, cl.name, time, cl.classroom_id, cl.teacher_id FROM classes cl " +
            "JOIN course_reg co ON co.course_id = cl.id" +
            " WHERE cl.term = #{term} AND cl.week_start <= #{week} AND cl.week_end >= #{week} AND co.student_id = #{id} AND cl.status = 1")
    List<ClassDTO> getClassScheduleSdu(String id, String term, Integer week);

    List<ClassListDTO> searchTeacherCourses(int id, String term, int offset, Integer pageSize, String keyword);

    @Select("SELECT published FROM classes WHERE id = #{courseId}")
    boolean isGradeReleased(@Param("courseId") Integer courseId);

    @Select("SELECT COUNT(*) FROM classes WHERE teacher_id = #{teacherId} AND status = 1")
    Integer getMyActiveClassCount(String id);

    @Select("SELECT COUNT(*) FROM classes WHERE teacher_id = #{teacherId} AND status = 0")
    Integer getMyPendingClassCount(String id);

    void updateCourseSchedule(@Param("id") Integer id,
                              @Param("timeSlot") String timeSlot,  // ← 注意改成 String
                              @Param("roomId") Integer roomId);

    @Select("SELECT * FROM classes WHERE term = #{term} AND status = 1")
    List<Classes> getActiveCoursesByTerm(@Param("term") String term);



}
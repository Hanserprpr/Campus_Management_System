package com.orbithy.cms.mapper;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.cms.data.po.ClassCourse;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
@TableName("class_course")
public interface ClassCourseMapper extends BaseMapper<ClassCourse> {

    @Insert("INSERT INTO class_course (class_id, course_id) VALUES (#{classId}, #{courseId})")
    void insertClassCourse(Integer classId, Integer courseId);

    @Select("SELECT COUNT(*) FROM class_course WHERE course_id = #{courseId}")
    Integer countClassCourse(Integer courseId);

    @Select("SELECT * FROM class_course WHERE class_id = #{classId} AND course_id = #{courseId}")
    ClassCourse selectClassCourse(Integer classId, Integer courseId);
}

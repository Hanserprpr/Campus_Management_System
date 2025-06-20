package com.orbithy.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.cms.data.po.Section;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SectionMapper extends BaseMapper<Section> {


    Integer getSectionCount(@Param("grade") String grade,
                            @Param("keyword") String keyword);


    @Select("select id from section where grade=#{grade}")
    List<Integer> getSectionIdList(String grade);


    List<Section> getSectionList(@Param("grade") String grade,
                                 @Param("offset") int offset,
                                 @Param("size") int size,
                                 @Param("keyword") String keyword);

    @Select("SELECT * FROM section LIMIT #{size} OFFSET #{offset}")
    List<Section> getSectionListAll(int offset, int size);

    List<Section> searchSection(String grade, Integer major, Integer number);

    @Select("SELECT count(*) FROM section")
    Integer getSectionCountAll();

    @Select("SELECT id FROM section WHERE grade = #{grade} AND major = #{major}")
    List<Integer> getSectionIdListByGradeAndMajor(@Param("grade") String grade, @Param("major") String major);

    @Select("SELECT id FROM section WHERE major = #{major} AND number = #{number}")
    String getSectionIdByGradeMajorAndNumber(@Param("major") int major, @Param("number") String number);

    @Select("SELECT s.grade FROM class_course cc JOIN section s ON cc.class_id = s.id WHERE cc.course_id = #{courseId}")
    Integer getGradeByClassId(int courseId);
}

package com.orbithy.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.cms.data.po.Section;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SectionMapper extends BaseMapper<Section> {

    @Select("SELECT COUNT(*) FROM section s JOIN class_course cc ON s.id = cc.course_id " +
            "JOIN classes cl ON cl.id = cc.class_id " +
            "WHERE grade = #{grade} AND cl.name LIKE CONCAT('%', #{keyword}, '%')")
    Integer getSectionCount(@Param("grade") String grade,
                            @Param("keyword") String keyword);


    @Select("select id from section where grade=#{grade}")
    List<Integer> getSectionIdList(String grade);

    @Select("SELECT * FROM section s JOIN class_course cc ON s.id = cc.course_id " +
            "JOIN classes cl ON cl.id = cc.class_id " +
            "s.grade = #{grade} AND cl.name LIKE CONCAT('%', #{keyword}, '%') " +
            "LIMIT #{size} OFFSET #{offset}")
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

}

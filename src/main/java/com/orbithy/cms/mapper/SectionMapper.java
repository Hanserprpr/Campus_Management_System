package com.orbithy.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.cms.data.po.Section;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SectionMapper extends BaseMapper<Section> {

    @Select("select count(*) from section where grade=#{grade}")
    Integer getSectionCount(String grade);

    @Select("select id from section where grade=#{grade}")
    List<Integer> getSectionIdList(String grade);

    @Select("SELECT * FROM section WHERE grade = #{grade} LIMIT #{size} OFFSET #{offset}")
    List<Section> getSectionList(String grade, int offset, int size);

    @Select("SELECT * FROM section LIMIT #{size} OFFSET #{offset}")
    List<Section> getSectionListAll(int offset, int size);

    List<Section> searchSection(String grade, Integer major, Integer number);

    @Select("SELECT count(*) FROM section")
    Integer getSectionCountAll();

    @Select("SELECT id FROM section WHERE grade = #{grade} AND major = #{major}")
    List<Integer> getSectionIdListByGradeAndMajor(@Param("grade") String grade, @Param("major") String major);

}

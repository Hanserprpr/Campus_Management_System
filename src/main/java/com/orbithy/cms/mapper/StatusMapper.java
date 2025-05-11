package com.orbithy.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.cms.data.dto.SectionCountDTO;
import com.orbithy.cms.data.po.Status;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StatusMapper extends BaseMapper<Status> {

    @Insert("INSERT INTO status (id, grade, admission, graduation) VALUES (#{id}, #{grade}, #{admission}, #{graduation})")
    void insertStatus(Status status);
    @Select("SELECT * FROM status WHERE grade=#{grade}")
    List<Status> getStatusList(String grade);

    @Update("UPDATE status SET section = #{section} WHERE id = #{id}")
    void updateStudentSection(@Param("id") int id, @Param("section") int section);

    @Select("SELECT id, grade, section, status, admission, graduation FROM status WHERE id=#{id}")
    Status getStatusById(String id);

    List<SectionCountDTO> getStudentCountBySectionIds(@Param("sectionIds") List<Integer> sectionIds);

    void insertBatch(@Param("list") List<Status> list);

    @Select("SELECT id FROM status WHERE section = #{sectionId}")
    List<Integer> getStudentIdsBySectionId(Integer sectionId);
}

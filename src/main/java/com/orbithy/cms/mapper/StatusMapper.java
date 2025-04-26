package com.orbithy.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.cms.data.po.Status;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

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

    Map<Integer, Integer> getStudentCountBySectionIds(@Param("sectionIds") List<Integer> sectionIds);


}

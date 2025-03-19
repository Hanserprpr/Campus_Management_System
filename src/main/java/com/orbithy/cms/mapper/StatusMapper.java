package com.orbithy.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.cms.data.po.Status;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StatusMapper extends BaseMapper<Status> {

    @Insert("INSERT INTO status (id, grade) VALUES (#{id}, #{grade})")
    void insertStatus(@Param("id") String id, @Param("grade") String grade);
    @Select("SELECT * FROM status WHERE grade=#{grade}")
    List<Status> getStatusList(String grade);

    @Update("UPDATE status SET section = #{section} WHERE id = #{id}")
    void updateStudentSection(@Param("id") int id, @Param("section") int section);
}

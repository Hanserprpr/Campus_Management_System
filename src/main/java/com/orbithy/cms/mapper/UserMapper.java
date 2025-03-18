package com.orbithy.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.cms.data.po.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select id from user where student_id=#{SDUId}")
    Integer getUserId(String SDUId);
    @Select("select * from user where id=#{id}")
    User getUserInfo(String id);
    @Select("select permission from user where id=#{id}")
    int getPermission(String id);
    @Select("select password from user where student_id=#{SDUId}")
    String getPassword(String SDUId);

    @Insert("INSERT INTO user (username, password, student_id, major) VALUES (#{username}, #{password}, #{SDUId}, '0')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addUser(User user);

}

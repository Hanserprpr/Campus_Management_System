package com.orbithy.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.cms.data.po.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select id from user where student_id=#{SDUId}")
    Integer getUserId(String SDUId);
    @Select("select id, username, email, sex, phone, student_id, major, permission, nation, ethnic from user where id=#{id}")
    User getUserInfo(String id);
    @Select("select permission from user where id=#{id}")
    int getPermission(String id);
    @Select("select password from user where student_id=#{SDUId}")
    String getPassword(String SDUId);
    @Select("select permission from user where id=#{id}")
    Integer getPermissionById(String id);

    Map<Integer, String> getUsernamesByIds(@Param("ids") List<String> ids);


    @Insert("INSERT INTO user (username, password, student_id, major) VALUES (#{username}, #{password}, #{SDUId}, '0')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addUser(User user);

    @Select("SELECT username, permission FROM user WHERE id = #{Id}")
    User getUserById(String Id);

    @Update("UPDATE user SET phone = #{phone} WHERE id = #{userId}")
    void updatePhone(@Param("userId") String userId, @Param("phone") String phone);

    @Update("UPDATE user SET email = #{email} WHERE id = #{userId}")
    void updateEmail(@Param("userId") String userId, @Param("email") String email);

    @Select("SELECT username,FROM user WHERE id = #{Id}")
    String getUsernameById(@Param("userId") String userId);
}

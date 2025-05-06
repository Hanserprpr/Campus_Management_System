package com.orbithy.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.cms.data.po.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select id from user where SDUId=#{SDUId}")
    Integer getUserId(String SDUId);
    @Select("select id, username, email, sex, phone, SDUId, major, permission, nation, ethnic, PoliticsStatus, college from user where id=#{id}")
    User getUserInfo(String id);
    @Select("select permission from user where id=#{id}")
    int getPermission(String id);
    @Select("select password from user where SDUId=#{SDUId}")
    String getPassword(String SDUId);
    @Select("select permission from user where id=#{id}")
    Integer getPermissionById(String id);

    List<Map<String, Object>> getUsernamesByIds(@Param("ids") List<String> ids);


    @Insert("INSERT INTO user (username, password, SDUId, major) VALUES (#{username}, #{password}, #{SDUId}, '0')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addUser(User user);

    @Select("SELECT username, permission FROM user WHERE id = #{Id}")
    User getUserById(String Id);

    @Update("UPDATE user SET phone = #{phone} WHERE id = #{userId}")
    void updatePhone(@Param("userId") String userId, @Param("phone") String phone);

    @Update("UPDATE user SET email = #{email} WHERE id = #{userId}")
    void updateEmail(@Param("userId") String userId, @Param("email") String email);

    @Select("SELECT username FROM user WHERE id = #{userId}")
    String getUsernameById(@Param("userId") int userId);

    @MapKey("id")
    Map<Integer, User> getUserNamesByIds(@Param("ids") List<Integer> ids);

    @Update("UPDATE user SET password = #{passwd} WHERE id = #{userId}")
    void resetPassword(String userId, String passwd);

    @Select("SELECT id, username,sex , email, SDUId, college FROM user WHERE permission = 1 AND college = #{college} LIMIT #{offset} ,#{size}")
    List<User> getTeacherList(String college, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT id, username,sex , email, SDUId, college FROM user WHERE permission = 1 LIMIT #{offset} ,#{size}")
    List<User> getTeacherListAll(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM user WHERE permission = 1")
    int countAllTeachers();

    @Select("SELECT COUNT(*) FROM user WHERE permission = 1 AND college = #{college}")
    int countTeachersByCollege(@Param("college") String college);

    void insertBatch(@Param("list") List<User> users);

    @Select("SELECT * FROM user WHERE (SDUId LIKE CONCAT('%', #{keyword}, '%') OR username LIKE CONCAT('%', #{keyword}, '%')) AND permission = #{permission} LIMIT #{offset}, #{size}")
    List<User> searchUsers(@Param("keyword") String keyword, @Param("permission") Integer permission, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM user WHERE (SDUId LIKE CONCAT('%', #{keyword}, '%') OR username LIKE CONCAT('%', #{keyword}, '%')) AND permission = #{permission}")
    int countSearchUsers(@Param("keyword") String keyword, @Param("permission") Integer permission);


    @Select("SELECT COUNT(*) FROM user WHERE permission = 2")
    int getStudentNum();

    @Select("SELECT COUNT(*) FROM user WHERE permission = 1")
    int getTeacherNum();

    @Select("SELECT u.SDUId AS SDUId, u.username, u.sex, u.major, s.grade, s.section, s.status " +
            "FROM user u " +
            "LEFT JOIN status s ON u.id = s.id " +
            "WHERE u.permission = 2 " +
            "AND (#{major} IS NULL OR u.major = #{major}) " +
            "AND (#{grade} IS NULL OR s.grade = #{grade}) " +
            "AND (#{status} IS NULL OR s.status = #{status}) " +
            "LIMIT #{offset}, #{size}")
    List<com.orbithy.cms.data.dto.StudentListDTO> getStudentListByPage(
            @Param("grade") Integer grade,
            @Param("major") String major,
            @Param("status") Integer status,
            @Param("offset") int offset,
            @Param("size") int size);

    @Select("SELECT COUNT(*) " +
            "FROM user u " +
            "LEFT JOIN status s ON u.id = s.id " +
            "WHERE u.permission = 2 " +
            "AND (#{major} IS NULL OR u.major = #{major}) " +
            "AND (#{grade} IS NULL OR s.grade = #{grade}) " +
            "AND (#{status} IS NULL OR s.status = #{status})")
    int countStudentList(
            @Param("grade") Integer grade,
            @Param("major") String major,
            @Param("status") Integer status);
}

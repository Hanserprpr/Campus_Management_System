package com.orbithy.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.cms.data.dto.StudentListDTO;
import com.orbithy.cms.data.po.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select id from user where SDUId=#{SDUId}")
    Integer getUserId(String SDUId);
    @Select("select id, username, email, sex, phone, SDUId, major, permission, nation, ethnic, PoliticsStatus, college from user where id=#{id}")
    User getUserInfo(String id);
    @Select("select permission from user where id=#{id}")
    Integer getPermission(String id);
    @Select("select password from user where SDUId=#{SDUId}")
    String getPassword(String SDUId);
    @Select("select permission from user where id=#{id}")
    Integer getPermissionById(String id);

    @Insert("INSERT INTO user (username, password, SDUId, major, email, permission, sex, college, ethnic, PoliticsStatus, phone) VALUES (#{username}, #{password}, #{SDUId}, '0', #{email}, #{permission}, #{sex}, #{college}, #{ethnic} , #{PoliticsStatus}, #{phone})")
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

    List<User> getUsersByIds(@Param("ids") List<Integer> ids);

    @Update("UPDATE user SET password = #{passwd} WHERE id = #{userId}")
    void resetPassword(String userId, String passwd);

    @Select("SELECT id, username,sex , email, SDUId, college FROM user WHERE permission = 1 AND college = #{college} LIMIT #{offset} ,#{size}")
    List<User> getTeacherList(String college, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT id, username,sex , email, SDUId, college FROM user WHERE permission = 1 LIMIT #{offset} ,#{size}")
    List<User> getTeacherListAll(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM user WHERE permission = 1")
    Integer countAllTeachers();

    @Select("SELECT COUNT(*) FROM user WHERE permission = 1 AND college = #{college}")
    Integer countTeachersByCollege(@Param("college") String college);

    void insertBatch(@Param("list") List<User> users);

    List<StudentListDTO> searchUsers(@Param("keyword") String keyword, @Param("permission") Integer permission, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM user WHERE (SDUId LIKE CONCAT('%', #{keyword}, '%') OR username LIKE CONCAT('%', #{keyword}, '%')) AND permission = #{permission}")
    Integer countSearchUsers(@Param("keyword") String keyword, @Param("permission") Integer permission);


    @Select("SELECT COUNT(*) FROM user WHERE permission = 2")
    Integer getStudentNum();

    @Select("SELECT COUNT(*) FROM user WHERE permission = 1")
    Integer getTeacherNum();

    List<com.orbithy.cms.data.dto.StudentListDTO> getStudentListByPage(
            @Param("grade") Integer grade,
            @Param("major") String major,
            @Param("status") Integer status,
            @Param("offset") int offset,
            @Param("size") int size);

    Integer countStudentList(
            @Param("grade") Integer grade,
            @Param("major") String major,
            @Param("status") Integer status);

    @Select("SELECT password FROM user WHERE id = #{userId}")
    String getPasswordById(String userId);

    @Select("SELECT COUNT(*) FROM classes WHERE teacher_id = #{teacherId} AND status = 1 AND term = #{term}")
    Integer getClassAmoByTeacherId(String teacherId, String term);

    @Select("SELECT SUM(period) FROM classes WHERE teacher_id = #{teacherId} AND status = 1 AND term = #{term}")
    Integer getTotalClassHour(String teacherId, String term);

    @Select("SELECT email FROM user WHERE id = #{userId}")
    String getEmailById(String userId);

    @Select("SELECT GPA_Rank FROM user WHERE id = #{userId}")
    int getGPARankById(String userId);
}

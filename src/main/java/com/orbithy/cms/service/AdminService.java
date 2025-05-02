package com.orbithy.cms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.orbithy.cms.data.dto.StudentCardDTO;
import com.orbithy.cms.data.dto.StudentListDTO;
import com.orbithy.cms.data.dto.UserListDTO;
import com.orbithy.cms.data.po.Status;
import com.orbithy.cms.data.po.User;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.mapper.StatusMapper;
import com.orbithy.cms.mapper.UserMapper;
import com.orbithy.cms.utils.BcryptUtils;
import com.orbithy.cms.utils.ResponseUtil;
import com.orbithy.cms.utils.WrapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StatusMapper statusMapper;
    @Autowired
    private LoginService loginService;

    public ResponseEntity<Result> getTeacherList(String college, int page, int limit) {
        int offset = (page - 1) * limit;
        if (college == null || college.isEmpty()) {
            int count = userMapper.countAllTeachers();
            int pages = count / limit + (count % limit == 0 ? 0 : 1);
            UserListDTO userListDTO = new UserListDTO();
            userListDTO.setUser(userMapper.getTeacherListAll(offset, limit));
            userListDTO.setPage(pages);
            return ResponseUtil.build(Result.success(userListDTO, "获取教师列表成功"));
        }
        int count = userMapper.countTeachersByCollege(college);
        int pages = count / limit + (count % limit == 0 ? 0 : 1);
        UserListDTO userListDTO = new UserListDTO();
        userListDTO.setUser(userMapper.getTeacherList(college, offset, limit));
        userListDTO.setPage(pages);
        return ResponseUtil.build(Result.success(userListDTO, "获取教师列表成功"));
    }

    public ResponseEntity<Result> getStudentList(Integer grade, String major, Integer status, Integer pageNum, Integer pageSize) {
        try {

            // 构建查询条件
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("permission", 2); // 只查询学生


            // 分页查询
            Page<User> page = new Page<>(pageNum, pageSize);
            Page<User> userPage = userMapper.selectPage(page, queryWrapper);

            // 转换为 DTO
            List<StudentListDTO> studentList = userPage.getRecords().stream()
                    .map(user -> {
                        StudentListDTO dto = new StudentListDTO();
                        dto.setSDUId(user.getSDUId());
                        dto.setUsername(user.getUsername());
                        dto.setSex(user.getSex());
                        dto.setMajor(String.valueOf(user.getMajor()));

                        // 获取学籍信息
                        Status studentStatus = statusMapper.getStatusById(user.getId().toString());
                        if (studentStatus != null) {
                            dto.setGrade(studentStatus.getGrade());
                            dto.setSection(studentStatus.getSection());
                            dto.setStatus(studentStatus.getStatus());
                        }

                        return dto;


                    })
                    .filter(dto -> {
                        // 应用过滤条件
                        if (grade != null && !grade.equals(dto.getGrade())) {
                            return false;
                        }
                        if (major != null && !major.isEmpty() && !major.equals(dto.getMajor())) {
                            return false;
                        }
                        if (status != null && !status.equals(dto.getStatus())) {//TODO
                            return false;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());

            // 构建分页结果
            Map<String, Object> result = new HashMap<>();
            result.put("list", studentList);
            result.put("total", userPage.getTotal());
            result.put("pages", userPage.getPages());
            result.put("current", userPage.getCurrent());
            result.put("size", userPage.getSize());

            return ResponseUtil.build(Result.success(result, "获取学生列表成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(500, "获取学生列表失败：" + e.getMessage()));
        }
    }


    public ResponseEntity<Result> searchUsers(String keyword, Integer permission) {
        try {
            List<User> users = userMapper.searchUsers(keyword, permission);
            return ResponseUtil.build(Result.success(users, "搜索成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(500, "搜索失败：" + e.getMessage()));
        }
    }

    public ResponseEntity<Result> getPeopleNum(int permission) {
        if (permission == 1) {
            return ResponseUtil.build(Result.success(userMapper.getTeacherNum(), "获取教师数量成功"));
        } else if (permission == 2) {
            return ResponseUtil.build(Result.success(userMapper.getStudentNum(), "获取学生数量成功"));
        } else {
            return ResponseUtil.build(Result.error(400, ""));
        }
    }

    public ResponseEntity<Result> deleteUser(String userId) {
        if (userMapper.getPermission(userId) == 0) {
            return ResponseUtil.build(Result.error(403, "无权限"));
        }
        userMapper.deleteById(userId);
        return ResponseUtil.build(Result.ok());
    }

    public ResponseEntity<Result> getUserInfo(String userId) {
        User user = userMapper.getUserInfo(userId);
        if (user == null) {
            return ResponseUtil.build(Result.error(404, "用户不存在"));
        }
        else if  (user.getPermission() == 0) {
            return ResponseUtil.build(Result.error(403, "无权限"));
        }
        Status status = statusMapper.getStatusById(userId);
        StudentCardDTO userCardDTO = new StudentCardDTO();
        userCardDTO.setUser(user);
        userCardDTO.setStatus(status);
        return ResponseUtil.build(Result.success(userCardDTO, "获取成功"));
    }

    @Transactional
    public ResponseEntity<Result> updateUser(User user, Status status) {
        UpdateWrapper<User> updateWrapper = WrapperUtil.buildNonNullUpdateWrapper(user, "id", user.getId());
        userMapper.update(null, updateWrapper);
        UpdateWrapper<Status> statusUpdateWrapper = WrapperUtil.buildNonNullUpdateWrapper(status, "id", status.getId());
        statusMapper.update(null, statusUpdateWrapper);
        return ResponseUtil.build(Result.ok());
    }

    public ResponseEntity<Result> addUser(User user) {
        String SDUId = user.getSDUId();
        String password = user.getPassword();
        if (!loginService.isExisted(SDUId)) {
            String passwd = BcryptUtils.encrypt(password);
            user.setPassword(passwd);
            userMapper.insert(user);
        }
        return ResponseUtil.build(Result.ok());
    }
}

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
import com.orbithy.cms.mapper.SectionMapper;
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
    @Autowired
    private SectionMapper sectionMapper;

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
            int offset = (pageNum - 1) * pageSize;
            List<StudentListDTO> studentList = userMapper.getStudentListByPage(grade, major, status, offset, pageSize);
            int total = userMapper.countStudentList(grade, major, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", studentList);
            result.put("total", total);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);
            int pages = (int) Math.ceil((double) total / pageSize);
            result.put("pages", pages);

            return ResponseUtil.build(Result.success(result, "获取学生列表成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(500, "获取学生列表失败：" + e.getMessage()));
        }
    }


    public ResponseEntity<Result> searchUsers(String keyword, Integer permission, int pageNum, int pageSize) {
        try {
            int offset = (pageNum - 1) * pageSize;
            List<StudentListDTO> users = userMapper.searchUsers(keyword, permission, offset, pageSize);
            int total = userMapper.countSearchUsers(keyword, permission);
            Map<String, Object> result = new HashMap<>();
            result.put("list", users);
            result.put("total", total);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);
            return ResponseUtil.build(Result.success(result, "搜索成功"));
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
        if (user.getPermission() == 1) {
            Status status = new Status();
            status.setGrade(Integer.valueOf(user.getSDUId().substring(0,4)));
            StudentCardDTO userCardDTO = new StudentCardDTO();
            userCardDTO.setStatus(status);
            userCardDTO.setUser(user);
            return ResponseUtil.build(Result.success(userCardDTO, "获取成功"));
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
        userMapper.update(user, updateWrapper);
        status.setSection(sectionMapper.getSectionIdByGradeMajorAndNumber(user.getMajor().getCode(), status.getSection()));
        UpdateWrapper<Status> statusUpdateWrapper = WrapperUtil.buildNonNullUpdateWrapper(status, "id", status.getId());
        statusMapper.update(status, statusUpdateWrapper);
        return ResponseUtil.build(Result.ok());
    }

    public ResponseEntity<Result> addUser(User user) {
        String SDUId = user.getSDUId();
        String password = user.getPassword();
        if (!loginService.isExisted(SDUId)) {
            if (user.getPermission() == 0) {
                return ResponseUtil.build(Result.error(403, "无权限"));
            }
            String passwd = BcryptUtils.encrypt(password);
            user.setPassword(passwd);
            if (user.getEmail() == null) {
                user.setEmail(SDUId + "@sdu.edu.cn");
            }
            userMapper.addUser(user);
            Status status = new Status();
            Integer grade = Integer.valueOf(SDUId.substring(0, 4));
            status.setId(user.getId());
            status.setGrade(grade);
            status.setAdmission(grade);
            status.setGraduation(grade+4);
            statusMapper.insertStatus(status);
            return ResponseUtil.build(Result.ok());
        }
        return ResponseUtil.build(Result.error(400, "用户已存在"));
    }
}

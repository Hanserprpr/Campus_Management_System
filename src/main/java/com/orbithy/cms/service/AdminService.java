package com.orbithy.cms.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.orbithy.cms.data.dto.StudentListDTO;
import com.orbithy.cms.data.po.Status;
import com.orbithy.cms.data.po.User;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.mapper.StatusMapper;
import com.orbithy.cms.mapper.UserMapper;
import com.orbithy.cms.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    public ResponseEntity<Result> getTeacherList() {
        return ResponseUtil.build(Result.success(userMapper.getTeacherList(), "获取教师列表成功"));
    }

    public ResponseEntity<Result> getStudentList(Integer grade, String major, Integer status, Integer pageNum, Integer pageSize) {
        try {

            // 构建查询条件
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("permission", 2); // 只查询学生

            // 添加筛选条件
            if (grade != null) {
                queryWrapper.eq("grade", grade);
            }
            if (major != null && !major.isEmpty()) {
                queryWrapper.eq("major", major);
            }
            if (status != null) {
                queryWrapper.eq("status", status);
            }

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

    public ResponseEntity<Result> getPeopleNum(int permission) {
        if (permission == 1) {
            return ResponseUtil.build(Result.success(userMapper.getTeacherNum(), "获取教师数量成功"));
        }
        else if (permission == 2) {
            return ResponseUtil.build(Result.success(userMapper.getStudentNum(), "获取学生数量成功"));
        }
        else {
            return ResponseUtil.build(Result.error(400, ""));
        }
    }
}

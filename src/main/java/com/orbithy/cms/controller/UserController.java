package com.orbithy.cms.controller;


import com.orbithy.cms.annotation.Admin;
import com.orbithy.cms.annotation.Auth;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.service.UserService;
import com.orbithy.cms.utils.ResponseUtil;
import com.orbithy.cms.data.po.User;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UserService userService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 添加教师
     *
     * @param user      用户信息
     * @param secret    密钥
     * @return ResponseEntity<Result>
     */
    @PostMapping("/addTeacher")
    public ResponseEntity<Result> addTeacher(User user, String secret) {
        return userService.addTeacher(user, secret);
    }

    /**
     * 获取个人信息
     * @return ResponseEntity<Result>
     */
    @Auth
    @PostMapping("/getInfo")
    public ResponseEntity<Result> getInfo() {
        String userId = (String) request.getAttribute("userId");
        return userService.getInfo(userId);
    }

    /**
     * 用户登出
     * @return 登出结果
     */
    @Auth
    @PostMapping("/logout")
    public ResponseEntity<Result> logout() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String authorizationHeader = request.getHeader("Authorization");
        String token;
        if (authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        } else {
            return ResponseUtil.build(Result.error(401, "token格式错误"));
        }
        return userService.logout(token);
    }

    /**
     * 更新用户手机号
     *
     * @param phone 新手机号
     * @return ResponseEntity<Result>
     */
    @Auth
    @PostMapping("/updatePhone")
    public ResponseEntity<Result> updatePhone(@RequestParam String phone) {
        String userId = (String) request.getAttribute("userId");
        return userService.updatePhone(userId, phone);
    }

    /**
     * 更新用户邮箱
     *
     * @param email 新邮箱
     * @return ResponseEntity<Result>
     */
    @Auth
    @PostMapping("/updateEmail")
    public ResponseEntity<Result> updateEmail(@RequestParam String email) {
        String userId = (String) request.getAttribute("userId");
        return userService.updateEmail(userId, email);
    }

    /**
     * 管理修改学生信息
     *
     *
     */
    @Admin
    @PostMapping("/updateStudent")
    public ResponseEntity<Result> updateStudent(User user) {
        return userService.updateStudent(user);
    }

    /**
     * 获取学籍卡片
     *
     * @return ResponseEntity<Result>
     */
    @Auth
    @PostMapping("/getStatusCard")
    public ResponseEntity<Result> getStatusCard() {
        String userId = (String) request.getAttribute("userId");
        return userService.getStudentCard(userId);
    }

    /**
     * 重置密码
     *
     * @param userId 用户ID
     * @return ResponseEntity<Result>
     */
    @Admin
    @PostMapping("/resetPassword")
    public ResponseEntity<Result> resetPassword(@RequestParam String userId) {
        return userService.resetPassword(userId);
    }

    /**
     * 获取学生列表
     * @param grade 年级
     * @param major 专业
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 学生列表
     */
    @Admin
    @GetMapping("/student/list")
    public ResponseEntity<Result> getStudentList(
            @RequestParam(required = false) Integer grade,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        String id = (String) request.getAttribute("userId");
        return userService.getStudentList(id, grade, major, status, pageNum, pageSize);
    }
}

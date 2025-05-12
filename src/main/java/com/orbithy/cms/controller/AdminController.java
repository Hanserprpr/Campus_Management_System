package com.orbithy.cms.controller;

import com.orbithy.cms.annotation.Admin;
import com.orbithy.cms.data.po.Status;
import com.orbithy.cms.data.po.User;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.service.AdminService;
import com.orbithy.cms.service.TermService;
import com.orbithy.cms.service.UserImportService;
import com.orbithy.cms.service.UserService;
import com.orbithy.cms.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserImportService userImportService;
    @Autowired
    private TermService termService;

    /**
     * 获取教师列表
     */
    @Admin
    @RequestMapping("/getTeacherList")
    public ResponseEntity<Result> getTeacherList(@RequestParam(required = false) String college,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(required = false, defaultValue = "10") int limit) {
        return adminService.getTeacherList(college, page, limit);
    }

    @Admin
    @PostMapping("/upload")
    public ResponseEntity<Result> uploadExcel(@RequestParam("file") MultipartFile file) throws Exception {
        userImportService.importUsersFromExcel(file);
        return ResponseUtil.build(Result.ok());
    }

    /**
     * 获取学生列表
     *
     * @param grade    年级
     * @param major    专业
     * @param status   状态
     * @param pageNum  页码
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
        return adminService.getStudentList(grade, major, status, pageNum, pageSize);
    }
    /**
     * 搜索用户
     *
     * @param keyword    搜索关键词（学工号或姓名）
     * @param permission 用户权限（12）
     * @param pageNum    页码
     * @param pageSize   每页大小
     * @return 用户列表
     */
    @Admin
    @GetMapping("/searchSdu")
    public ResponseEntity<Result> searchUser(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "0") Integer permission,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return adminService.searchUsers(keyword, permission, pageNum, pageSize);
    }

    /**
     * 获取学生、教师人数
     */
    @Admin
    @GetMapping("/getNum")
    public ResponseEntity<Result> getNum(int permission) {
        return adminService.getPeopleNum(permission);
    }

    /**
     * 删除用户
     */
    @Admin
    @PostMapping("/deleteUser")
    public ResponseEntity<Result> deleteUser(@RequestParam String userId) {
        return adminService.deleteUser(userId);
    }

    /**
     * 获取用户信息
     */
    @Admin
    @GetMapping("/getUserInfo")
    public ResponseEntity<Result> getUserInfo(@RequestParam String userId) {
        return adminService.getUserInfo(userId);
    }

    /**
     * 修改用户信息
     */
    @Admin
    @PostMapping("/updateUser")
    public ResponseEntity<Result> updateUser(@ModelAttribute User user,
                                             @ModelAttribute Status status) {
        return adminService.updateUser(user, status);
    }

    /**
     * 添加用户
     */
    @Admin
    @PostMapping("/addUser")
    public ResponseEntity<Result> addUser(@ModelAttribute User user) {
        return adminService.addUser(user);
    }
}

package com.orbithy.cms.controller;

import com.orbithy.cms.annotation.Admin;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.service.AdminService;
import com.orbithy.cms.service.UserImportService;
import com.orbithy.cms.service.UserService;
import com.orbithy.cms.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserImportService userImportService;

    /**
     * 获取教师列表
     */
    @Admin
    @RequestMapping("/getTeacherList")
    public ResponseEntity<Result> getTeacherList() {
        return adminService.getTeacherList();
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
}

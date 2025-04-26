package com.orbithy.cms.controller;

import com.orbithy.cms.annotation.Admin;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.service.AdminService;
import com.orbithy.cms.service.UserImportService;
import com.orbithy.cms.service.UserService;
import com.orbithy.cms.utils.ResponseUtil;
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
}

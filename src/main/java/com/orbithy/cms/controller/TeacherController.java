package com.orbithy.cms.controller;

import com.orbithy.cms.annotation.Auth;
import com.orbithy.cms.annotation.Teacher;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.service.TeacherService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/Teacher")
public class TeacherController {
    @Resource
    private TeacherService TeacherService;
    @Autowired
    private HttpServletRequest request;

    @Teacher
    @GetMapping("/getMessage")
    public ResponseEntity<Result> getMessage() throws IOException {
        String id = (String) request.getAttribute("userId");
        return TeacherService.getMessage(id);
    }

    /**
     * 获取教师课程数量
     *
     * @return ResponseEntity<Result>
     */
    @Teacher
    @GetMapping("/countClass")
    public ResponseEntity<Result> getCountClass(String term) throws IOException {
        String id = (String) request.getAttribute("userId");
        return TeacherService.getCountClass(id, term);
    }

    /**
     * 获取教室列表
     */
    @Teacher
    @GetMapping("/getClassRoom")
    public ResponseEntity<Result> getClassRoom() {
        return TeacherService.getClassRoom();
    }

    /**
     * 获取教师统计信息
     * @param term 学期
     * @return ResponseEntity<Result>
     */
    @Auth
    @PostMapping("/getMessage")
    public ResponseEntity<Result> getMessage(String term) {
        String id = (String) request.getAttribute("userId");
        return teacherService.getMessage(id,term);
    }

}


package com.orbithy.cms.controller;


import com.orbithy.cms.annotation.Teacher;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.service.TeacherService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Teacheer")
public class TeacherController {
    @Resource
    private TeacherService TeacherService;
    @Autowired
    private HttpServletRequest request;

    @Teacher
    @GetMapping("/getMessage")
    public ResponseEntity<Result> getMessage() {
        String id = (String) request.getAttribute("userId");
        return TeacherService.getMessage(id);
    }

}

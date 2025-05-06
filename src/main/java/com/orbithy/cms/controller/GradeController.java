package com.orbithy.cms.controller;

import com.orbithy.cms.annotation.Admin;
import com.orbithy.cms.annotation.Auth;
import com.orbithy.cms.data.po.Grade;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.service.GradeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/grade")
public class GradeController {
    @Autowired
    private GradeService gradeService;
    @Autowired
    private HttpServletRequest request;

    // 添加成绩
    @Auth
    @PostMapping("/setGrade")
    public ResponseEntity<Result> setGrade(Grade gradeDTO) {
        String id = (String) request.getAttribute("userId");
        return gradeService.setGrade(id, gradeDTO);
    }

    @Auth
    @PostMapping("/getGradeList")
    public ResponseEntity<Result> getGradeList(int courseId) {
        String id = (String) request.getAttribute("userId");
        return gradeService.getGradeList(id, courseId);
    }

    @Admin
    @PostMapping("/releaseGrade")
    public ResponseEntity<Result> releaseGrade(int courseId) {
        return gradeService.releaseGrade(courseId);
    }

    @Auth
    @GetMapping("/getGrade")
    public ResponseEntity<Result> getGrade(String term) {
        String id = (String) request.getAttribute("userId");
        return gradeService.getGrade(id, term);
    }
}

package com.orbithy.cms.controller;

import com.orbithy.cms.annotation.Auth;
import com.orbithy.cms.annotation.Admin;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.service.CourseSelectionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course-selection")
public class CourseSelectionController {
    @Autowired
    private CourseSelectionService courseSelectionService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 开始选课（教务权限）
     */
    @Admin
    @PostMapping("/start")
    public ResponseEntity<Result> startSelection(@RequestParam String term) {
        return courseSelectionService.startSelection(term);
    }

    /**
     * 结束选课（教务权限）
     */
    @Admin
    @PostMapping("/end")
    public ResponseEntity<Result> endSelection() {
        return courseSelectionService.endSelection();
    }

    /**
     * 搜索课程
     */
    @Auth
    @GetMapping("/search")
    public ResponseEntity<Result> searchCourses(@RequestParam String keyword) {
        return courseSelectionService.searchCourses(keyword);
    }

    /**
     * 选课
     */
    @Auth
    @PostMapping("/select/{courseId}")
    public ResponseEntity<Result> selectCourse(@PathVariable Integer courseId) {
        String userId = request.getHeader("userId");
        return courseSelectionService.selectCourse(Integer.parseInt(userId), courseId);
    }

    /**
     * 查询选课结果
     */
    @Auth
    @GetMapping("/results")
    public ResponseEntity<Result> getSelectionResults() {
        String userId = request.getHeader("userId");
        return courseSelectionService.getSelectionResults(Integer.parseInt(userId));
    }
}

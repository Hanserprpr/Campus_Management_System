package com.orbithy.cms.controller;

import com.orbithy.cms.annotation.Auth;
import com.orbithy.cms.annotation.Admin;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.service.CourseSelectionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/course-selection")
public class CourseSelectionController {

    @Autowired
    private CourseSelectionService courseSelectionService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 搜索课程
     */
    @Auth
    @GetMapping("/search")
    public ResponseEntity<Result> searchCourses(@RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "term", required = false) String term, @RequestParam(required = false) String type) throws IOException {
        String userId = (String) request.getAttribute("userId");
        return courseSelectionService.searchCourses(userId, keyword, term, type);
    }

    /**
     * 选课
     */
    @Auth
    @PostMapping("/select/{courseId}")
    public ResponseEntity<Result> selectCourse(@PathVariable Integer courseId) {
        String userId = (String) request.getAttribute("userId");
        return courseSelectionService.selectCourse(userId, courseId);
    }

    /**
     * 退选课程
     */
    @Auth
    @PostMapping("/drop/{courseId}")
    public ResponseEntity<Result> dropCourse(@PathVariable Integer courseId) {
        String userId = (String) request.getAttribute("userId");
        return courseSelectionService.dropCourse(userId, courseId);
    }

    /**
     * 查询选课结果
     */
    @Auth
    @GetMapping("/results")
    public ResponseEntity<Result> getSelectionResults() {
        String userId = (String) request.getAttribute("userId");
        return courseSelectionService.getSelectionResults(userId);
    }

    /**
     * 获取未选课程
     */
    @Auth
    @GetMapping("/unChoose")
    public ResponseEntity<Result> unChooseCourse() throws IOException {
        String userId = (String) request.getAttribute("userId");
        return courseSelectionService.getUnSelectResult(userId);
    }
}

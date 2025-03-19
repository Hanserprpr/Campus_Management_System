package com.orbithy.cms.controller;

import com.orbithy.cms.annotation.Auth;
import com.orbithy.cms.data.dto.CreateCourseDTO;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.service.ClassService;
import com.orbithy.cms.utils.ResponseUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/class")
public class ClassController {
    @Resource
    private ClassService classService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 教师创建课程
     *
     * @param courseDTO 课程信息
     * @return ResponseEntity<Result>
     */
    @Auth
    @PostMapping("/create")
    public ResponseEntity<Result> createCourse(@RequestHeader("userId") String userId,
                                             @RequestBody CreateCourseDTO courseDTO) {
        return classService.createCourse(userId, courseDTO);
    }

    /**
     * 教务审批课程
     *
     * @param courseId 课程ID
     * @param status 审批状态（1通过，2拒绝）
     * @return ResponseEntity<Result>
     */
    @Auth
    @PostMapping("/approve/{courseId}")
    public ResponseEntity<Result> approveCourse(@RequestHeader("userId") String userId,
                                              @PathVariable Integer courseId,
                                              @RequestParam Integer status,
                                              @RequestParam(required = false) String classNum) {
        return classService.approveCourse(userId, courseId, status, classNum);
    }

    /**
     * 获取课程列表
     * 支持按学期筛选
     *
     * @param term 学期（可选，格式：YYYY-YYYY-S，例如：2023-2024-1）
     * @return ResponseEntity<Result>
     */
    @Auth
    @GetMapping("/list")
    public ResponseEntity<Result> getCourseList(@RequestHeader("userId") String userId,
                                              @RequestParam(required = false) String term) {
        return classService.getCourseList(userId, term);
    }

    /**
     * 获取课程详情
     *
     * @param courseId 课程ID
     * @return ResponseEntity<Result>
     */
    @Auth
    @GetMapping("/detail/{courseId}")
    public ResponseEntity<Result> getCourseDetail(@RequestHeader("userId") String userId,
                                                @PathVariable Integer courseId) {
        return classService.getCourseDetail(userId, courseId);
    }

    /**
     * 教师修改课程信息
     *
     * @param courseId 课程ID
     * @param courseDTO 课程信息
     * @return ResponseEntity<Result>
     */
    @Auth
    @PostMapping("/update/{courseId}")
    public ResponseEntity<Result> updateCourse(@RequestHeader("userId") String userId,
                                             @PathVariable Integer courseId,
                                             @RequestBody CreateCourseDTO courseDTO) {
        return classService.updateCourse(userId, courseId, courseDTO);
    }

    /**
     * 教师删除课程
     *
     * @param courseId 课程ID
     * @return ResponseEntity<Result>
     */
    @Auth
    @PostMapping("/delete/{courseId}")
    public ResponseEntity<Result> deleteCourse(@RequestHeader("userId") String userId,
                                             @PathVariable Integer courseId) {
        return classService.deleteCourse(userId, courseId);
    }
}

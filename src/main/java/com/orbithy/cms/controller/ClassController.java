package com.orbithy.cms.controller;

import com.orbithy.cms.annotation.Admin;
import com.orbithy.cms.annotation.Auth;
import com.orbithy.cms.annotation.Teacher;
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
    @Teacher
    @PostMapping("/create")
    public ResponseEntity<Result> createCourse(@RequestBody CreateCourseDTO courseDTO) {
        String id = (String) request.getAttribute("userId");
        return classService.createCourse(id, courseDTO);
    }

    /**
     * 教务审批课程
     *
     * @param courseId 课程ID
     * @param status 审批状态（1通过，2拒绝）
     * @param classNum 课序号（通过时必填）
     * @param reason 拒绝理由（拒绝时必填）
     * @return ResponseEntity<Result>
     */
    @Admin
    @PostMapping("/approve/{courseId}")
    public ResponseEntity<Result> approveCourse(@PathVariable Integer courseId,
                                              @RequestParam Integer status,
                                              @RequestParam(required = false) String classNum,
                                              @RequestParam(required = false) String reason) {
        String id = (String) request.getAttribute("userId");
        return classService.approveCourse(id, courseId, status, classNum, reason);
    }

    /**
     * 获取课程列表
     * 支持按学期筛选
     *
     * @param term 学期（可选，格式：YYYY-YYYY-S，例如：2023-2024-1）
     * @return ResponseEntity<Result>
     */
    @Teacher
    @GetMapping("/list")
    public ResponseEntity<Result> getCourseList(@RequestParam(required = false) String term,
                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        String id = (String) request.getAttribute("userId");
        return classService.getCourseList(id, term, pageNum, pageSize);
    }

    /**
     * 获取课程详情
     *
     * @param courseId 课程ID
     * @return ResponseEntity<Result>
     */
    @Teacher
    @GetMapping("/detail/{courseId}")
    public ResponseEntity<Result> getCourseDetail(@PathVariable Integer courseId) {
        String id = (String) request.getAttribute("userId");
        return classService.getCourseDetail(id, courseId);
    }

    /**
     * 教师修改课程信息
     *
     * @param courseId 课程ID
     * @param courseDTO 课程信息
     * @return ResponseEntity<Result>
     */
    @Teacher
    @PostMapping("/update/{courseId}")
    public ResponseEntity<Result> updateCourse(@PathVariable Integer courseId,
                                             @RequestBody CreateCourseDTO courseDTO) {
        String id = (String) request.getAttribute("userId");
        return classService.updateCourse(id, courseId, courseDTO);
    }

    /**
     * 教师删除课程
     *
     * @param courseId 课程ID
     * @return ResponseEntity<Result>
     */
    @Teacher
    @PostMapping("/delete/{courseId}")
    public ResponseEntity<Result> deleteCourse(@PathVariable Integer courseId) {
        String id = (String) request.getAttribute("userId");
        return classService.deleteCourse(id, courseId);
    }

    /**
     * 获取待批准的课程列表
     */
    @Admin
    @GetMapping("/pending")
    public ResponseEntity<Result> getPendingCourses() {
        return classService.getPendingCourses();
    }

    /**
     * 获取已选课成员名单
     */
    @Teacher
    @GetMapping("/{courseId}/students")
    public ResponseEntity<Result> getSelectedStudents(@PathVariable Integer courseId) {
        String id = (String) request.getAttribute("userId");
        int permission = (int)request.getAttribute("permission");
        return classService.getSelectedStudents(permission, id, courseId);
    }

    /**
     *排课
     */
    @Admin
    @PostMapping("/autoSchedule")
    public ResponseEntity<Result> autoSchedule(@RequestParam String term) {
        String id = (String) request.getAttribute("userId");
        return classService.autoSchedule(id, term);
    }

    /**
     *删除已审核课程
     */
    @Admin
    @PostMapping("/deleteAd/{courseId}")
    public ResponseEntity<Result> deleteAd(@PathVariable Integer courseId) {
        String id = (String) request.getAttribute("userId");
        return classService.adminDeleteCourse(id, courseId);
    }

    /**
     * 获取课程被退回原因
     *
     * @param courseId 课程ID
     * @return ResponseEntity<Result>
     */
    @Teacher
    @GetMapping("/getReason/{courseId}")
    public ResponseEntity<Result> getReason(@PathVariable Integer courseId) {
        String id = (String) request.getAttribute("userId");
        return classService.getReason(id, courseId);
    }

    /**
     * 获取课表
     */
    @Auth
    @GetMapping("/getClassSchedule/{week}")
    public ResponseEntity<Result> getClassSchedule(@RequestParam String term,
                                                @PathVariable Integer week) {
    String id = (String) request.getAttribute("userId");
    return classService.getClassSchedule(id,week,term);
    }
}

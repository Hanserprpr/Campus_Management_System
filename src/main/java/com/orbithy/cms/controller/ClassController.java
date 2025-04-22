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
     * ��ʦ�����γ�
     *
     * @param courseDTO �γ���Ϣ
     * @return ResponseEntity<Result>
     */
    @Auth
    @PostMapping("/create")
    public ResponseEntity<Result> createCourse(@RequestBody CreateCourseDTO courseDTO) {
        String userId = request.getHeader("userId");
        return classService.createCourse(userId, courseDTO);
    }

    /**
     * ���������γ�
     *
     * @param courseId �γ�ID
     * @param status ����״̬��1ͨ����2�ܾ���
     * @param classNum ����ţ�ͨ��ʱ���
     * @param reason �ܾ����ɣ��ܾ�ʱ���
     * @return ResponseEntity<Result>
     */
    @Auth
    @PostMapping("/approve/{courseId}")
    public ResponseEntity<Result> approveCourse(@PathVariable Integer courseId,
                                              @RequestParam Integer status,
                                              @RequestParam(required = false) String classNum,
                                              @RequestParam(required = false) String reason) {
        String userId = request.getHeader("userId");
        return classService.approveCourse(userId, courseId, status, classNum, reason);
    }

    /**
     * ��ȡ�γ��б�
     * ֧�ְ�ѧ��ɸѡ
     *
     * @param term ѧ�ڣ���ѡ����ʽ��YYYY-YYYY-S�����磺2023-2024-1��
     * @return ResponseEntity<Result>
     */
    @Auth
    @GetMapping("/list")
    public ResponseEntity<Result> getCourseList(@RequestParam(required = false) String term) {
        String userId = request.getHeader("userId");
        return classService.getCourseList(userId, term);
    }

    /**
     * ��ȡ�γ�����
     *
     * @param courseId �γ�ID
     * @return ResponseEntity<Result>
     */
    @Auth
    @GetMapping("/detail/{courseId}")
    public ResponseEntity<Result> getCourseDetail(@PathVariable Integer courseId) {
        String userId = request.getHeader("userId");
        return classService.getCourseDetail(userId, courseId);
    }

    /**
     * ��ʦ�޸Ŀγ���Ϣ
     *
     * @param courseId �γ�ID
     * @param courseDTO �γ���Ϣ
     * @return ResponseEntity<Result>
     */
    @Auth
    @PostMapping("/update/{courseId}")
    public ResponseEntity<Result> updateCourse(@PathVariable Integer courseId,
                                             @RequestBody CreateCourseDTO courseDTO) {
        String userId = request.getHeader("userId");
        return classService.updateCourse(userId, courseId, courseDTO);
    }

    /**
     * ��ʦɾ���γ�
     *
     * @param courseId �γ�ID
     * @return ResponseEntity<Result>
     */
    @Auth
    @PostMapping("/delete/{courseId}")
    public ResponseEntity<Result> deleteCourse(@PathVariable Integer courseId) {
        String userId = request.getHeader("userId");
        return classService.deleteCourse(userId, courseId);
    }

    /**
     * ��ȡ����׼�Ŀγ��б�
     */
    @Auth
    @GetMapping("/pending")
    public ResponseEntity<Result> getPendingCourses() {
        String userId = request.getHeader("userId");
        return classService.getPendingCourses(userId);
    }
}

package com.orbithy.cms.controller;

import com.orbithy.cms.annotation.Admin;
import com.orbithy.cms.annotation.Auth;
import com.orbithy.cms.annotation.Teacher;
import com.orbithy.cms.data.po.Notice;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.service.NoticeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/notice")
public class NoticeController {
    @Resource
    private NoticeService noticeService;
    @Resource
    private HttpServletRequest request;

    /**
     * 发布公告
     *
     * @param notice 公告
     * @return ResponseEntity<Result>
     */
    @Teacher
    @RequestMapping("/set")
    public ResponseEntity<Result> setNotice(Notice notice) {
        String userId = (String) request.getAttribute("userId");
        int permission = (int) request.getAttribute("permission");
        return noticeService.setNotice(userId, permission, notice);
    }

    /**
     * 学生获取公告列表
     *
     * @return ResponseEntity<Result>
     */
    @Auth
    @RequestMapping("/getStudentNoticeList")
    public ResponseEntity<Result> getStudentNoticeList() {
        return noticeService.getNoticeList(2, 1);
    }

    /**
     * 教师获取公告列表
     *
     * @return ResponseEntity<Result>
     */
    @Teacher
    @RequestMapping("/getTeacherNoticeList")
    public ResponseEntity<Result> getTeacherNoticeList() {
        return noticeService.getNoticeList(1, 1);
    }

    /**
     * 教务获取公告列表
     *
     * @param Status 公告状态
     * @return ResponseEntity<Result>
     */
    @Admin
    @RequestMapping("/getAdminNoticeList")
    public ResponseEntity<Result> getAdminNoticeList(int Status) {
        return noticeService.getNoticeList(0, Status);
    }

    /**
     * 编辑公告
     *
     * @param notice 公告
     * @return ResponseEntity<Result>
     */
    @Teacher
    @RequestMapping("/edit")
    public ResponseEntity<Result> editNotice(Notice notice) {
        String userId = (String) request.getAttribute("userId");
        return noticeService.editNotice(notice, userId);
    }

    /**
     * 删除公告
     */
    @Teacher
    @RequestMapping("/close")
    public ResponseEntity<Result> closeNotice(Integer id){
        String userId = (String) request.getAttribute("userId");
    return noticeService.closeNotice(userId,id);
    }
}

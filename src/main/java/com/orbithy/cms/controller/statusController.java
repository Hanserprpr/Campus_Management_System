package com.orbithy.cms.controller;

import com.orbithy.cms.annotation.Auth;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.mapper.UserMapper;
import com.orbithy.cms.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/status")
public class statusController {

    @Autowired
    private UserService userService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 设置用户学籍
     * @param userId 学号
     * @param status 状态
     * @return ResponseEntity<Result>
     */
    @Auth
    @PostMapping("/set")
    public ResponseEntity<Result> setStatus(String userId, String status) {
        String teacherId = (String) request.getAttribute("userId");
        return userService.setUserStatus(teacherId, userId, status);
    }

    /**
     * 获取学籍卡片
     *
     * @return ResponseEntity<Result>
     */
    @Auth
    @PostMapping("/getStatusCard")
    public ResponseEntity<Result> getStatusCard() {
        String userId = (String) request.getAttribute("userId");
        return userService.getStudentCard(userId);
    }

}

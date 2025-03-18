package com.orbithy.cms.controller;


import com.orbithy.cms.annotation.Auth;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.service.UserService;
import com.orbithy.cms.utils.ResponseUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UserService userService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 添加教师
     *
     * @param SDUId     教师工号
     * @param password  密码
     * @param username  用户名
     * @param permission 权限
     * @param secret    密钥
     * @return ResponseEntity<Result>
     */
    @PostMapping("/addTeacher")
    public ResponseEntity<Result> addTeacher(String SDUId, String password, String username, int permission, String secret) {
        return userService.addTeacher(SDUId, password, username, permission, secret);
    }

    /**
     * 获取个人信息
     * @return ResponseEntity<Result>
     */
    @Auth
    @PostMapping("/getInfo")
    public ResponseEntity<Result> getInfo() {
        String userId = (String) request.getAttribute("userId");
        return userService.getInfo(userId);
    }

    /**
     * 用户登出
     * @return 登出结果
     */
    @Auth
    @PostMapping("/logout")
    public ResponseEntity<Result> logout() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String authorizationHeader = request.getHeader("Authorization");
        String token;
        if (authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        } else {
            return ResponseUtil.build(Result.error(401, "token格式错误"));
        }
        return userService.logout(token);
    }

    /**
     * 更新用户手机号
     *
     * @param phone 新手机号
     * @return ResponseEntity<Result>
     */
    @Auth
    @PostMapping("/updatePhone")
    public ResponseEntity<Result> updatePhone(@RequestParam String phone) {
        String userId = (String) request.getAttribute("userId");
        return userService.updatePhone(userId, phone);
    }

    /**
     * 更新用户邮箱
     *
     * @param email 新邮箱
     * @return ResponseEntity<Result>
     */
    @Auth
    @PostMapping("/updateEmail")
    public ResponseEntity<Result> updateEmail(@RequestParam String email) {
        String userId = (String) request.getAttribute("userId");
        return userService.updateEmail(userId, email);
    }
}

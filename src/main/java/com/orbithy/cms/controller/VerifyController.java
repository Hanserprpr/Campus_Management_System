package com.orbithy.cms.controller;

import com.orbithy.cms.annotation.Auth;
import com.orbithy.cms.data.dto.VerifyRequestDTO;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.service.VerifyService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/verify")
public class VerifyController {
    @Resource
    private HttpServletRequest request;
    @Resource
    private VerifyService verifyService;


    /**
     * 获取验证码
     */
    @Auth
    @GetMapping("/getCode")
    public ResponseEntity<Result> getCode(@RequestParam(required = false) String email) {
        String userId = (String) request.getAttribute("userId");
        return verifyService.getCode(userId, email);
    }

    /**
     * 验证验证码
     */
    @Auth
    @PostMapping("/verifyCode")
    public ResponseEntity<Result> verify(@RequestBody VerifyRequestDTO verifyRequest) {
        String ticket = verifyRequest.getTicket();
        String code = verifyRequest.getCode();
        return verifyService.verify(ticket, code);
    }
}

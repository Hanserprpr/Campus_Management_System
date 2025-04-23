package com.orbithy.cms.controller;

import com.orbithy.cms.annotation.refreshAuth;
import com.orbithy.cms.data.dto.LoginRequestDTO;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.service.LoginService;
import com.orbithy.cms.service.SDULogin;
import com.orbithy.cms.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private SDULogin sduLogin;
    @Autowired
    private LoginService loginService;
    @Autowired
    private HttpServletRequest request;

    @RequestMapping(value = "/SDULogin", method = {RequestMethod.POST})
    public ResponseEntity<Result> SDULogin(HttpServletRequest request, HttpServletResponse response, @RequestBody LoginRequestDTO loginRequestDTO) {
        String stuId = null;
        String password = null;
        try {
            stuId = loginRequestDTO.getStuId();
            password = loginRequestDTO.getPassword();
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400,"Bad Requests"));
        }
        return sduLogin.SDUIdentify(stuId,password);
    }

    @PostMapping("/simpleLogin")
    public ResponseEntity<Result> simpleLogin(@RequestBody LoginRequestDTO loginRequestDTO) {
        String stuId = null;
        String password = null;
        try {
            stuId = loginRequestDTO.getStuId();
            password = loginRequestDTO.getPassword();
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(400,"Bad Requests"));
        }
        return loginService.login(stuId,password);
    }

    @refreshAuth
    @PostMapping("/refreshToken")
    public ResponseEntity<Result> refresh(){
        String userId = (String) request.getAttribute("userId");
        return loginService.refresh(userId);
    }
}

package com.orbithy.cms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbithy.cms.annotation.refreshAuth;
import com.orbithy.cms.data.dto.LoginRequestDTO;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.service.LoginService;
import com.orbithy.cms.service.SDULogin;
import com.orbithy.cms.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.casbin.casdoor.service.AuthService;
import org.casbin.casdoor.entity.User;
import org.casbin.casdoor.config.Config;

import java.io.IOException;

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
    @Autowired
    private AuthService authService;
    @Autowired
    private Config config;

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

    @Value("${casdoor.redirect-uri}")
    private String redirectUri;

    @GetMapping("/toLogin")
    public void toLogin(@RequestParam String deviceId, HttpServletResponse response) throws IOException {
        String loginUrl = authService.getSigninUrl(redirectUri, deviceId);
        response.sendRedirect(loginUrl);
    }


    @GetMapping("/callback")
    public ResponseEntity<String> OAuthLogin(@RequestParam String code, @RequestParam String state, HttpServletRequest request) throws JsonProcessingException {
        return loginService.OAuthLogin(code, state, request);

    }

    @GetMapping("/getOAuthToken")
    public ResponseEntity<Result> getOAuthToken(@RequestParam String state) {
        return loginService.getOAuthToken(state);
    }


    @refreshAuth
    @PostMapping("/refreshToken")
    public ResponseEntity<Result> refresh(){
        String userId = (String) request.getAttribute("userId");
        return loginService.refresh(userId);
    }

}

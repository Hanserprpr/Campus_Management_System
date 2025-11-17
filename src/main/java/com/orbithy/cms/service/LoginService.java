package com.orbithy.cms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbithy.cms.cache.IGlobalCache;
import com.orbithy.cms.data.po.Status;
import com.orbithy.cms.data.po.User;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.mapper.StatusMapper;
import com.orbithy.cms.mapper.UserMapper;
import com.orbithy.cms.utils.BcryptUtils;
import com.orbithy.cms.utils.JWTUtil;
import com.orbithy.cms.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.casbin.casdoor.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.orbithy.cms.utils.JWTUtil.REFRESH_SECRET_KEY;

@Service
public class LoginService {

    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AuthService authService;
    @Autowired
    private StatusMapper statusMapper;
    @Autowired
    private IGlobalCache redis;

    /**
     * 获取token
     *
     * @param id 用户id
     * @return Map<String, String>
     */
    public Map<String, String> getToken(String id) {
        String token;
        String refreshToken;
        refreshToken = jwtUtil.getToken(id, JWTUtil.REFRESH_EXPIRE_TIME, JWTUtil.REFRESH_SECRET_KEY);

        token = jwtUtil.getToken(id, JWTUtil.EXPIRE_TIME, JWTUtil.SECRET_KEY);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", token);
        tokenMap.put("refreshToken", refreshToken);
        User user = getDetail(id);
        tokenMap.put("username", user.getUsername());
        tokenMap.put("permission", user.getPermission().toString());
        return tokenMap;
    }

    private User getDetail (String stuId) {
        return userMapper.getUserById(stuId);
    }

    boolean isExisted(String stuId) {
        Integer count = userMapper.getUserId(stuId);
        return count != null && count > 0;
    }

    public ResponseEntity<Result> login(String stuId, String password) {
        if (!isExisted(stuId)) {
            return ResponseUtil.build(Result.error(401, "用户不存在"));
        }
        String passwd = userMapper.getPassword(stuId);
        if (!BcryptUtils.verifyPasswd(password, passwd)) {
            return ResponseUtil.build(Result.error(401, "密码错误"));
        }
        String userId = String.valueOf(userMapper.getUserId(stuId));
        return ResponseUtil.build(Result.success(getToken(userId), "登录成功"));
    }

    /**
     * 刷新token
     * @param userId 刷新用token
     * @return ResponseEntity<Result>
     */
    public ResponseEntity<Result> refresh(String userId) {
        try {
            String newAccessToken = jwtUtil.getToken(userId, JWTUtil.EXPIRE_TIME, JWTUtil.SECRET_KEY);
            Map<String, String> map = new HashMap<>();
            map.put("accessToken", newAccessToken);
            String msg;
            msg = "成功！已获取新accessToken";
            return ResponseUtil.build(new Result(200, map, msg));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseUtil.build(Result.error(400, "无有效RefreshToken"));
    }

    @Transactional
    public ResponseEntity<String> OAuthLogin(String code, String state, HttpServletRequest request) {
        // 1. 获取 token
        String token = authService.getOAuthToken(code, state);

        // 2. 解析 token 得到用户信息
        org.casbin.casdoor.entity.User user = authService.parseJwtToken(token);
        String SDUId = user.education;
        if (!this.isExisted(SDUId)) {
            // 3. 如果用户不存在，注册用户
            User newUser = new User();
            newUser.setEmail(user.email);
            newUser.setUsername(user.displayName);
            newUser.setNation(user.region);
            newUser.setSDUId(SDUId);
            newUser.setSex(user.gender);
            userMapper.insert(newUser);
            Status status = new Status();
            Integer grade = Integer.valueOf(SDUId.substring(2, 4));
            status.setId(newUser.getId());
            status.setGrade(grade);
            status.setAdmission(grade);
            status.setGraduation(grade+4);
            System.out.println(status);
            statusMapper.insertStatus(status);
        }
        String id = Integer.toString(userMapper.getUserId(SDUId));
        Map<String, String> tokenMap = getToken(id);
        redis.set(state, tokenMap, 180);

        String html = """
        <!DOCTYPE html>
        <html lang="zh">
        <head>
            <meta charset="UTF-8">
            <title>登录成功</title>
            <style>
                body {
                    font-family: 'Segoe UI', sans-serif;
                    background-color: #f4f6f8;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    height: 100vh;
                    margin: 0;
                }
                .card {
                    background-color: #fff;
                    padding: 40px;
                    border-radius: 10px;
                    box-shadow: 0 6px 16px rgba(0,0,0,0.1);
                    text-align: center;
                }
                h1 {
                    color: #2ecc71;
                    margin-bottom: 16px;
                }
                p {
                    font-size: 16px;
                    color: #333;
                }
            </style>
        </head>
        <body>
            <div class="card">
                <h1>✅ 登录成功！</h1>
                <p>欢迎你，<strong>%s</strong>！</p>
                <p>您可以返回软件继续操作。</p>
            </div>
        </body>
        </html>
        """.formatted(user.displayName);

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(html);
    }

    public ResponseEntity<Result> getOAuthToken(String state) {
        return ResponseUtil.build(Result.success(redis.get(state), "获取成功"));
    }
}

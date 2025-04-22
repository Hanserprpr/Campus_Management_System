package com.orbithy.cms.service;

import com.orbithy.cms.data.po.User;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.mapper.UserMapper;
import com.orbithy.cms.utils.BcryptUtils;
import com.orbithy.cms.utils.JWTUtil;
import com.orbithy.cms.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.orbithy.cms.utils.JWTUtil.REFRESH_SECRET_KEY;

@Service
public class LoginService {

    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private UserMapper userMapper;

    /**
     * 获取token
     *
     * @param id 用户id
     * @return ResponseEntity<Result>
     */
    public ResponseEntity<Result> getToken(String id) {
        String token;
        String refreshToken;
        try {
            refreshToken = jwtUtil.getToken(id, JWTUtil.REFRESH_EXPIRE_TIME, REFRESH_SECRET_KEY);

            token = jwtUtil.getToken(id, JWTUtil.EXPIRE_TIME, JWTUtil.SECRET_KEY);

            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("accessToken", token);
            tokenMap.put("refreshToken", refreshToken);
            User user = getDetail(id);
            tokenMap.put("username", user.getUsername());
            tokenMap.put("permission", user.getPermission().toString());
            return ResponseUtil.build(Result.success(tokenMap, "登陆成功"));
        } catch (Exception e) {
            return ResponseUtil.build(Result.error(500, e.getMessage()));
        }
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
        return getToken(userId);
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

}

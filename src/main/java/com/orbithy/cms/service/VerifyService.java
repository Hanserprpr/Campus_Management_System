package com.orbithy.cms.service;

import com.orbithy.cms.cache.IGlobalCache;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.mapper.UserMapper;
import com.orbithy.cms.utils.EmailSender;
import com.orbithy.cms.utils.ResponseUtil;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
public class VerifyService {

    @Resource
    private IGlobalCache redis;

    @Resource
    private UserMapper userMapper;

    /**
     * 生成验证码和 ticket
     * @return 返回包含验证码和 ticket 的数组
     */
    private String[] generateVerification() {
        // 生成验证码（6 位数字）
        String code = String.format("%06d", (int) (Math.random() * 1000000));

        // 生成唯一的 ticket
        String ticket = UUID.randomUUID().toString();

        // 存储到 Redis 中，设置过期时间（例如 5 分钟）
        redis.set("VERIFICATION_CODE:" + ticket, code, 5 * 60);

        return new String[]{code, ticket};
    }

    /**
     * 校验验证码
     * @param ticket 唯一 ticket
     * @param code 用户输入的验证码
     * @return 校验结果
     */
    private boolean verifyCode(String ticket, String code) {
        // 获取 Redis 中存储的验证码
        String key = "VERIFICATION_CODE:" + ticket;
        Object storedCode = redis.get(key);

        if (storedCode != null && storedCode.equals(code)) {
            redis.del(key);
            return true;
        }
        return false;
    }

    public ResponseEntity<Result> getCode(String userId, String email) {
        if (email == null) {
            email = userMapper.getEmailById(userId);
        }
        String[] verify = this.generateVerification();
        String code = verify[0];
        String ticket = verify[1];
        try{
            // 发送邮件
            EmailSender.sendEmail(email, "教务系统验证码", "您的验证码是：" + code);}
        catch(Exception e){
            System.out.println(e.getMessage());
            return ResponseUtil.build(Result.error(554, "邮件发送失败"));
        }
        return ResponseUtil.build(Result.success(ticket, "验证码已发送"));
    }

    public ResponseEntity<Result> verify(String ticket, String code) {
        if (this.verifyCode(ticket, code)) {
            return ResponseUtil.build(Result.ok());
        } else {
            return ResponseUtil.build(Result.error(401, "验证码错误"));
        }
    }
}

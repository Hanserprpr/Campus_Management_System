package com.orbithy.cms.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    private Integer code; // 业务状态码
    private Object data;  // 数据
    private String msg;   // 提示信息

    /**
     * 返回成功结果
     */
    public static Result success(Object data, String msg) {
        return new Result(200, data, msg);
    }

    /**
     * 返回成功（无数据）
     */
    public static Result ok() {
        return new Result(200, null, "成功");
    }

    /**
     * 返回错误结果
     */
    public static Result error(Integer code, String msg) {
        return new Result(code, null, msg);
    }

    /**
     * 获取 HTTP 状态码
     */
    public HttpStatus httpStatus() {
        return switch (this.code) {
            case 400 -> HttpStatus.BAD_REQUEST; // 请求错误
            case 401 -> HttpStatus.UNAUTHORIZED; // 未授权
            case 403 -> HttpStatus.FORBIDDEN;    // 禁止访问
            case 404 -> HttpStatus.NOT_FOUND;    // 资源未找到
            case 409 -> HttpStatus.CONFLICT;     // 资源冲突
            case 418 -> HttpStatus.I_AM_A_TEAPOT; // 服务器拒绝请求
            case 500, 554 -> HttpStatus.INTERNAL_SERVER_ERROR; // 服务器错误
            default -> HttpStatus.OK; // 默认返回 200
        };
    }
}
package com.orbithy.cms.controller;

import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
@RequestMapping("/sys")
public class SysController {

    @Value("${app.version}")
    private String version;

    @GetMapping("/version")
    public ResponseEntity<Result> system() {
        return ResponseUtil.build(Result.success(version, "系统正常"));
    }
}

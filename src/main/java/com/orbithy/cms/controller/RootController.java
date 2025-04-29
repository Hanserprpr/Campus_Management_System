package com.orbithy.cms.controller;

import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.utils.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RootController {

    @GetMapping
    public ResponseEntity<Result> rootIsTeapot() {
        return ResponseUtil.build(Result.error(418, "I'm a teapot"));
    }
}
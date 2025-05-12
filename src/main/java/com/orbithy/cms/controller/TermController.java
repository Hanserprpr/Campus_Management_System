package com.orbithy.cms.controller;

import com.orbithy.cms.annotation.Admin;
import com.orbithy.cms.annotation.Auth;
import com.orbithy.cms.data.po.Term;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.service.TermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/term")
public class TermController {
    @Autowired
    private TermService termService;

    /**
     * 添加学期
     */
    @PostMapping("/addTerm")
    @Admin
    public ResponseEntity<Result> addTerm(String term) throws IOException {
        return termService.addTerm(term);
    }

    /**
     * 开放/关闭选课
     */
    @PostMapping("/editSelection")
    @Admin
    public ResponseEntity<Result> openSelection(Term term) throws IOException {
        return termService.updateTerm(term);
    }

    /**
     * 获取学期列表
     */
    @Auth
    @GetMapping("/getTermList")
    public ResponseEntity<Result> getTermList() throws IOException {
        return termService.getTerms();
    }
}

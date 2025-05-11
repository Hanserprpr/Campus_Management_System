package com.orbithy.cms.controller;

import com.orbithy.cms.annotation.Admin;
import com.orbithy.cms.data.po.Section;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/section")
public class SectionController {

    @Autowired
    private SectionService sectionService;

    /**
     * 添加班级
     *
     * @param section 班级信息
     * @return ResponseEntity<Result>
     */
    @Admin
    @PostMapping("/addSection")
    public ResponseEntity<Result> addSection(Section section) {
        return sectionService.addSection(section);
    }

    /**
     * 修改班级信息
     * @param section 班级信息
     * @return ResponseEntity<Result>
     */
    @Admin
    @PostMapping("/updateSection")
    public ResponseEntity<Result> updateSection(Section section) {
        return sectionService.updateSection(section);
    }

    /**
     * 分配人员
     *
     * @return ResponseEntity<Result>
     */
    @Admin
    @PostMapping("/assign")
    public ResponseEntity<Result> assign(String grade) {
        return sectionService.assign(grade);
    }

    /**
     * 删除班级
     *
     * @param id 班级信息
     * @return ResponseEntity<Result>
     */
    @Admin
    @PostMapping("/deleteSection")
    public ResponseEntity<Result> deleteSection(String id) {
        return sectionService.deleteSection(id);
    }

    /**
     * 根据年级获取班级列表
     *
     * @param grade 年级
     * @param page  页码
     * @param size  每页数量
     * @return ResponseEntity<Result>
     */
    @Admin
    @GetMapping("/getSectionList")
    public ResponseEntity<Result> getSectionList(String grade, int page, int size) {
        return sectionService.getSectionList(grade, page, size);
    }

    /**
     * 获取班级列表
     *
     * @param page 页码
     * @param size 每页数量
     * @return ResponseEntity<Result>
     */
    @Admin
    @GetMapping("/getSectionListAll")
    public ResponseEntity<Result> getSectionList(int page, int size) {
        return sectionService.getSectionListAll(page, size);
    }

    /**
     * 搜索班级
     *
     */
    @Admin
    @GetMapping("/search")
    public ResponseEntity<Result> searchSection(String grade, int major) {
        return sectionService.searchSection(grade, major);
    }

    /**
     * 获取班级成员
     *
     */
    @Admin
    @GetMapping("/getSectionMember")
    public ResponseEntity<Result> getSectionMember(Integer id) {
        return sectionService.getSectionMember(id);
    }

    /**
     * 获取班级信息
     *
     */
    @Admin
    @GetMapping("/getSectionInfo")
    public ResponseEntity<Result> getSectionInfo(Integer id) {
        return sectionService.getSectionInfo(id);
    }
}

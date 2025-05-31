package com.orbithy.cms.service;

import com.orbithy.cms.data.dto.SectionCountDTO;
import com.orbithy.cms.data.dto.SectionDTO;
import com.orbithy.cms.data.po.Section;
import com.orbithy.cms.data.po.Status;
import com.orbithy.cms.data.po.User;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.mapper.SectionMapper;
import com.orbithy.cms.mapper.StatusMapper;
import com.orbithy.cms.mapper.UserMapper;
import com.orbithy.cms.utils.ResponseUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SectionService {

    @Autowired
    private SectionMapper sectionMapper;
    @Autowired
    private StatusMapper statusMapper;
    @Autowired
    private UserMapper userMapper;

    public ResponseEntity<Result> addSection(Section section) {
        section.setId(null);
        sectionMapper.insert(section);
        return ResponseUtil.build(Result.ok());
    }

    public ResponseEntity<Result> assign(String grade) {
        String[] majors = {"0", "1", "2", "3"};
        for (String major : majors) {
            // 1. 获取该年级+专业的班级
            List<Integer> sectionList = sectionMapper.getSectionIdListByGradeAndMajor(grade, major);
            int classCount = sectionList.size();
            if (classCount == 0) {
                continue; // 没有班级就跳过该专业
            }

            // 2. 获取该年级+专业的学生
            List<Status> statusList = statusMapper.getStatusListByGradeAndMajor(grade, major);
            int index = 0;

            // 3. 轮流分配学生到班级
            for (Status status : statusList) {
                int cls = sectionList.get(index % classCount);
                statusMapper.updateStudentSection(status.getId(), cls);
                index++;
            }
        }

        return ResponseUtil.build(Result.ok());
    }


    public ResponseEntity<Result> updateSection(Section section) {
        sectionMapper.updateById(section);
        return ResponseUtil.build(Result.ok());
    }

    public ResponseEntity<Result> deleteSection(String id) {
        sectionMapper.deleteById(id);
        return ResponseUtil.build(Result.ok());
    }

    public ResponseEntity<Result> getSectionList(String grade, int page, int size, String keyword) {
        int offset = (page - 1) * size;
        List<Section> sectionList = sectionMapper.getSectionList(grade, offset, size, keyword);
        int count = sectionMapper.getSectionCount(grade, keyword);
        int pages = count / size + (count % size == 0 ? 0 : 1);
        return setAdvisorName(sectionList, pages);
    }

    public ResponseEntity<Result> getSectionListAll(int page, int size) {
        int offset = (page - 1) * size;
        List<Section> sectionList = sectionMapper.getSectionListAll(offset, size);
        int count = sectionMapper.getSectionCountAll();
        int pages = count / size + (count % size == 0 ? 0 : 1);
        return setAdvisorName(sectionList, pages);
    }

    @NotNull
    private ResponseEntity<Result> setAdvisorName(List<Section> sectionList, int pages) {
        List<Integer> sectionIds = sectionList.stream()
                .map(Section::getId)
                .toList();

        List<Integer> advisorIds = sectionList.stream()
                .map(Section::getAdvisorId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Integer, String> idToNameMap = advisorIds.isEmpty()
                ? new HashMap<>()
                : userMapper.getUsersByIds(advisorIds)
                .stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));

        Map<Integer, Integer> sectionIdToStudentCount =
                statusMapper.getStudentCountBySectionIds(sectionIds)
                        .stream()
                        .collect(Collectors.toMap(
                                SectionCountDTO::getSection,
                                SectionCountDTO::getStudentCount
                        ));

        List<SectionDTO> sectionDTOList = sectionList.stream()
                .map(section -> new SectionDTO(
                        section.getId(),
                        section.getMajor(),
                        section.getAdvisorId(),
                        section.getGrade(),
                        section.getNumber(),
                        idToNameMap.get(section.getAdvisorId()),
                        sectionIdToStudentCount.getOrDefault(section.getId(), 0)
                ))
                .toList();
        Map<String, Object> result = new HashMap<>();
        result.put("section", sectionDTOList);
        result.put("page", pages);
        return ResponseUtil.build(Result.success(result, "获取成功"));
    }

    public ResponseEntity<Result> searchSection(String grade ,Integer major, Integer number) {
        return ResponseUtil.build(Result.success(sectionMapper.searchSection(grade, major, number), "获取成功"));
    }

    public ResponseEntity<Result> getSectionMember(Integer id) {
        List<Integer> ids = statusMapper.getStudentIdsBySectionId(id);
        if (ids.isEmpty()) {
            return ResponseUtil.build(Result.error(404, "该班级暂无成员"));
        }
        List<User> users = userMapper.getUsersByIds(ids);
        return ResponseUtil.build(Result.success(users, "获取成功"));
    }

    public ResponseEntity<Result> getSectionInfo(Integer id) {
        Section section = sectionMapper.selectById(id);
        if (section == null) {
            return ResponseUtil.build(Result.error(404, "班级不存在"));
        }
        return ResponseUtil.build(Result.success(section, "获取成功"));
    }
}

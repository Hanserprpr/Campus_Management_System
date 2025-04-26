package com.orbithy.cms.service;

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
        int classCount = sectionMapper.getSectionCount(grade);
        if (classCount == 0) {
            return ResponseUtil.build(Result.error(400, "班级不存在"));
        }
        List<Status> statusList = statusMapper.getStatusList(grade);
        List<Integer> sectionList = sectionMapper.getSectionIdList(grade);
        int index = 0;
        for (Status status : statusList) {
            int cls = sectionList.get(index % classCount);
            statusMapper.updateStudentSection(status.getId(), cls);
            index++;
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

    public ResponseEntity<Result> getSectionList(String grade, int page, int size) {
        int offset = (page - 1) * size;
        List<Section> sectionList = sectionMapper.getSectionList(grade, offset, size);

        return setAdvisorName(sectionList);
    }

    public ResponseEntity<Result> getSectionListAll(int page, int size) {
        int offset = (page - 1) * size;
        List<Section> sectionList = sectionMapper.getSectionListAll(offset, size);
        return setAdvisorName(sectionList);
    }

    @NotNull
    private ResponseEntity<Result> setAdvisorName(List<Section> sectionList) {

        List<Integer> advisorIds = sectionList.stream()
                .map(Section::getAdvisorId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Integer, String> idToNameMap;
        if (advisorIds.isEmpty()) {
            idToNameMap = new HashMap<>();
        } else {
            idToNameMap = userMapper.getUserNamesByIds(advisorIds)
                    .values().stream()
                    .collect(Collectors.toMap(User::getId, User::getUsername));
        }

        List<SectionDTO> sectionDTOList = sectionList.stream()
                .map(section -> new SectionDTO(section.getId(), section.getMajor(), section.getAdvisorId(), section.getGrade(),  section.getNumber(), idToNameMap.get(section.getAdvisorId())))
                .toList();

        return ResponseUtil.build(Result.success(sectionDTOList, "获取成功"));
    }


}

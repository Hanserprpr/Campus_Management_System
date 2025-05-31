package com.orbithy.cms.service;

import com.orbithy.cms.data.po.Rooms;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.mapper.ClassMapper;
import com.orbithy.cms.mapper.RoomMapper;
import com.orbithy.cms.mapper.UserMapper;
import com.orbithy.cms.utils.ResponseUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeacherService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private ClassMapper classMapper;
    @Resource
    private RoomMapper roomMapper;

    public ResponseEntity<Result> getMessage(String id) throws IOException {
        if (userMapper.getPermission(id) !=1){
            return ResponseUtil.build(Result.error(403, "无权限"));
        }
        Map<String, Integer> Mes = new HashMap<>();
        String term = TermService.getCurrentTerm();
        int classAmo = userMapper.getClassAmoByTeacherId(id, term);
        Integer totalClassHour = userMapper.getTotalClassHour(id, term);
        if (totalClassHour == null) {
            totalClassHour = 0;
        }
        Mes.put("totalClassHour", totalClassHour);
        Mes.put("classAmo", classAmo);
        return ResponseUtil.build(Result.success(Mes,"CGA"));
    }

    public ResponseEntity<Result> getCountClass(String id, String term) throws IOException {
        Map<String, Integer> result = new HashMap<>();
        String currentTerm = TermService.getCurrentTerm();
        Integer activeClass = classMapper.getMyActiveClassCount(id, currentTerm);
        Integer pendingClass = classMapper.getMyPendingClassCount(id, term);
        result.put("activeClass", activeClass);
        result.put("pendingClass", pendingClass);
        return ResponseUtil.build(Result.success(result,"获取成功"));
    }

    public ResponseEntity<Result> getClassRoom() {
        List<Rooms> rooms = roomMapper.getAllClassRooms();
        if (rooms == null || rooms.isEmpty()) {
            return ResponseUtil.build(Result.error(404, "没有教室信息"));
        }
        return ResponseUtil.build(Result.success(rooms, "获取教室信息成功"));
    }
}

package com.orbithy.cms.service;

import com.orbithy.cms.data.po.Notice;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.mapper.NoticeMapper;
import com.orbithy.cms.mapper.UserMapper;
import com.orbithy.cms.utils.ResponseUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoticeService {
    @Resource
    private NoticeMapper noticeMapper;
    @Autowired
    private UserMapper userMapper;


    public ResponseEntity<Result> setNotice(String userId, int permission, Notice notice) {
        notice.setCreatorId(Integer.valueOf(userId));
        if (notice.getVisibleScope() == 1 && permission != 0) {
            return ResponseUtil.build(Result.error(403, "无权限"));
        } else if (notice.getVisibleScope() == 0) {
            return ResponseUtil.build(Result.error(403, "无权限"));
        }
        notice.setPublishTime(LocalDateTime.now());
        notice.setCreatedAt(LocalDateTime.now());
        noticeMapper.insert(notice);
        return ResponseUtil.build(Result.ok());
    }

    public ResponseEntity<Result> getNoticeList(int permission, int status) {
        List<Notice> noticeList = new ArrayList<>();
        if (permission == 2) {
            noticeList = noticeMapper.getNoticeList(2);
        } else if (permission == 0) {
            if (status == 0) {
                noticeList = noticeMapper.getNotice(status);
            } else {
                noticeList = noticeMapper.getNotice(status);//修改简单
            }
        } else {
            noticeList = noticeMapper.getNotice(1);
        }
        return ResponseUtil.build(Result.success(noticeList, "获取公告成功"));
    }

    public ResponseEntity<Result> editNotice(Notice notice) {
        if (notice.getId() == null) {
            return ResponseUtil.build(Result.error(404, "公告id未提供"));
        }
        noticeMapper.updateById(notice);
        return ResponseUtil.build(Result.ok());
    }

    public ResponseEntity<Result> closeNotice(String userId, int id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            return ResponseUtil.build(Result.error(403, "不存在"));
        }
        String nid = notice.getCreatorId().toString();
        if ((!nid.equals(userId) && userMapper.getPermissionById(userId) != 0)) {
            return ResponseUtil.build(Result.error(403, "无权限"));
        }
        noticeMapper.closeNotice(id);
        return ResponseUtil.build(Result.ok());
    }
}

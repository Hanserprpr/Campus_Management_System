package com.orbithy.cms.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.orbithy.cms.data.po.Notice;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.mapper.NoticeMapper;
import com.orbithy.cms.utils.ResponseUtil;
import com.orbithy.cms.utils.WrapperUtil;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoticeService {
    @Resource
    private NoticeMapper noticeMapper;


    public ResponseEntity<Result> setNotice(String userId, int permission, Notice notice) {
        notice.setCreatorId(Integer.valueOf(userId));
        if (notice.getVisibleScope() == 1 || permission != 0) {
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
                noticeList = noticeMapper.getNoticeListAll();
            }
        }
        return ResponseUtil.build(Result.success(noticeList, "获取公告成功"));
    }

    public ResponseEntity<Result> editNotice(Notice notice) {
        if (notice.getId() == null) {
            return ResponseUtil.build(Result.error(404, "公告id未提供"));
        }
        UpdateWrapper<Notice> updateWrapper = WrapperUtil.buildNonNullUpdateWrapper(notice, "id", notice.getId());
        noticeMapper.update(null, updateWrapper);
        return ResponseUtil.build(Result.ok());
    }
}

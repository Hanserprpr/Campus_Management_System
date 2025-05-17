package com.orbithy.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.cms.data.po.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
    @Select("SELECT no.* , use.creat_name AS username FROM notice no JOIN user use ON no.creator_id = use.id " +
            "WHERE visible_scope = #{permission} AND status = 1 ORDER BY publish_time DESC")
    List<Notice> getNoticeList(int permission);

    @Select("SELECT no.* , use.creat_name AS username FROM notice no JOIN user use ON no.creator_id = use.id " +
            "ORDER BY publish_time DESC")
    List<Notice> getNoticeListAll();

    @Select("SELECT no.* , use.creat_name AS username FROM notice no JOIN user use ON no.creator_id = use.id " +
            "WHERE status = #{status} ORDER BY publish_time DESC")
    List<Notice> getNotice(int status);
}

package com.orbithy.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orbithy.cms.data.po.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
    @Select("SELECT no.* , us.username AS username FROM notice no JOIN user us ON no.creator_id = us.id " +
            "WHERE visible_scope = #{permission} AND status = 1 ORDER BY publish_time DESC")
    List<Notice> getNoticeList(int permission);

    @Select("SELECT no.* , us.username AS username FROM notice no JOIN user us ON no.creator_id = us.id " +
            "ORDER BY publish_time DESC")
    List<Notice> getNoticeListAll();

    @Select("SELECT no.* , us.username AS username FROM notice no JOIN user us ON no.creator_id = us.id " +
            "WHERE status = #{status} ORDER BY publish_time DESC")
    List<Notice> getNotice(int status);

    @Update("UPDATE status = 0 FROM notice WHERE id = #{id}")
    void closeNotice(int id);


}

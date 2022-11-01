package com.lergo.framework.handler;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Slf4j
@Component
public class CUDObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("CUD insert fill [createtime|updatetime|deleted]");
        this.strictInsertFill(metaObject, "createTime", Timestamp.class, new Timestamp(DateUtil.current()));
        this.strictInsertFill(metaObject, "updateTime", Timestamp.class, new Timestamp(DateUtil.current()));
        this.strictInsertFill(metaObject, "deleted", Boolean.class, false);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("CUD update fill [updatetime]");
        this.strictUpdateFill(metaObject, "updateTime", Timestamp.class, new Timestamp(DateUtil.current()));
    }
}
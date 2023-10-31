package com.lergo.framework.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用于消除IDEA的警告
 */
@Mapper
public interface NoWarnMapper {

    @Select("SELECT VERSION();")
    String version();
}

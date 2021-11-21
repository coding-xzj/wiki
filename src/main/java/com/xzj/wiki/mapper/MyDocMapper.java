package com.xzj.wiki.mapper;

import org.apache.ibatis.annotations.Param;

public interface MyDocMapper {

    public void increaseViewCount(@Param("id") Long id);
}
package com.wanghengtong.threadpool.mapper;

import com.wanghengtong.threadpool.entity.UserDO;

import java.util.List;

public interface UserMapper {

    int batchInsert(List<UserDO> userDOList);

}

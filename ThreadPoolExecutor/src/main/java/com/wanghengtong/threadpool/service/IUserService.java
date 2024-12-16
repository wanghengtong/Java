package com.wanghengtong.threadpool.service;

import com.wanghengtong.threadpool.entity.UserDO;

import java.util.List;

public interface IUserService {

    int saveBatch(List<UserDO> entityList);

}

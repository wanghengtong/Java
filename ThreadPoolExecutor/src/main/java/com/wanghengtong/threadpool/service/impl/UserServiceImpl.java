package com.wanghengtong.threadpool.service.impl;

import com.wanghengtong.threadpool.entity.UserDO;
import com.wanghengtong.threadpool.mapper.UserMapper;
import com.wanghengtong.threadpool.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * @param entityList
     * @return
     */
    @Override
    public int saveBatch(List<UserDO> entityList) {
        return userMapper.batchInsert(entityList);
    }

}

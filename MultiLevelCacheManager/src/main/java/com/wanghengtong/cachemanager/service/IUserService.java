package com.wanghengtong.cachemanager.service;

import com.wanghengtong.cachemanager.bean.User;

/**
 * @author wanghengtong
 * @desc
 * @date 2025年06月23日 22:24
 */
public interface IUserService {
    void add(User user);

    User getById(String id);

    User update(User user);

    void deleteById(String id);
}

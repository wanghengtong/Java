package com.wanghengtong.cachemanager.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wanghengtong
 * @desc
 * @date 2025年06月23日 22:22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private String id;

    private String name;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}

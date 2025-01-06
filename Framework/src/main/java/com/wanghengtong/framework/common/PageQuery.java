package com.wanghengtong.framework.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wanghengtong
 * @desc 分页查询参数
 * @date 2024年12月31日 11:24
 */
@Data
public class PageQuery<T> implements Serializable {

    private Long current;

    private Long size;

    private T requestBody;

    @JsonCreator
    public PageQuery(@JsonProperty(value = "current", defaultValue = "1") Long current, @JsonProperty(value = "size", defaultValue = "10") Long size) {
        this.current = current != null ? current : 1L;
        this.size = size != null ? size : 10L;
    }

}

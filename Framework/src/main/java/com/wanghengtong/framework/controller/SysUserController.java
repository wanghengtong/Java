package com.wanghengtong.framework.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wanghengtong.framework.common.PageQuery;
import com.wanghengtong.framework.entity.SysUserDO;
import com.wanghengtong.framework.entity.SysUserRequest;
import com.wanghengtong.framework.entity.SysUserResponse;
import com.wanghengtong.framework.mapper.SysUserMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月31日 21:40
 */
@RestController
@RequestMapping("/api/sysUser")
public class SysUserController {

    @Resource
    private SysUserMapper sysUserMapper;

    @GetMapping("/save")
    public SysUserResponse save() {
        SysUserDO sysUser = new SysUserDO();
        sysUser.setUsername("admin");
        sysUser.setPassword("123456");
        sysUser.setNickname("test");
        sysUser.setEmail("admin@163.com");
        sysUser.setPhone("15100000000");
        sysUser.setAddress("北京王府井大街");
        sysUserMapper.insert(sysUser);
        return BeanUtil.copyProperties(sysUser, SysUserResponse.class);
    }

    @GetMapping("/update")
    public void update(Long id) {
        SysUserDO sysUser = new SysUserDO();
        sysUser.setId(id);
        sysUser.setUsername("修改的名字");
        sysUserMapper.updateById(sysUser);
    }

    @GetMapping("/list")
    public List<SysUserDO> list() {
        return sysUserMapper.selectList(null);
    }

    @GetMapping("/listByContion")
    public List<SysUserDO> listByContion() {
        return sysUserMapper.selectList(new LambdaQueryWrapper<SysUserDO>()
                // 查询年龄=11的
                .eq(SysUserDO::getAddress, "北京王府井大街")
                // 模糊匹配
                .like(SysUserDO::getUsername, "A")
                // 排序，按照创建时间
                .orderByDesc(SysUserDO::getCreateTime)
        );
    }

    @GetMapping("/getById")
    public SysUserResponse getById(Long id) {
        SysUserDO sysUserDO = sysUserMapper.selectById(id);
        return BeanUtil.copyProperties(sysUserDO, SysUserResponse.class);
    }

    @GetMapping("/delete")
    public void delete(Long id) {
        sysUserMapper.deleteById(id);
    }

    @GetMapping("/page")
    public IPage<SysUserDO> page(@RequestBody PageQuery<SysUserRequest> pageQuery) {
        LambdaQueryWrapper<SysUserDO> queryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(pageQuery.getRequestBody())) {
            SysUserRequest sysUserRequest = pageQuery.getRequestBody();
            queryWrapper.eq(SysUserDO::getUsername, sysUserRequest.getUsername());
        }
        IPage<SysUserDO> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());
        return sysUserMapper.selectPage(page, queryWrapper);
    }

}

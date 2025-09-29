package com.wanghengtong.kingbasees;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@SpringBootTest
class KingBaseEsApplicationTests {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JdbcTemplate jdbcTemplate; //Jdbc connection tool class

    @Test
    void selectList() throws JsonProcessingException {
        try {
            // 先检查表是否存在以及有多少条记录
            String countSql = "SELECT COUNT(*) FROM t_user";
            int count = jdbcTemplate.queryForObject(countSql, Integer.class);
            System.out.println("表中记录数: " + count);

            // 如果有记录再查询详细数据
            if (count > 0) {
                String sql = "SELECT * FROM t_user";
                List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
                System.out.println(objectMapper.writeValueAsString(result));
            } else {
                System.out.println("表中没有数据");
            }
        } catch (EmptyResultDataAccessException e) {
            System.out.println("查询结果为空: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("查询发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void selectAll()  {
        try {
            String sql = "SELECT * FROM t_user";
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
            System.out.println("查询到 " + result.size() + " 条记录");
            for (Map<String, Object> row : result) {
                System.out.println(objectMapper.writeValueAsString(row));
            }
        } catch (Exception e) {
            System.out.println("查询所有记录发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void insert() {
        try {
            String sql = "INSERT INTO t_user (id, name, age, mobile, email) VALUES (?, ?, ?, ?, ?)";
            Object[] params = {System.currentTimeMillis(), "测试用户", 25, "13800138000", "test@example.com"};
            int rows = jdbcTemplate.update(sql, params);
            System.out.println("插入了 " + rows + " 条记录");
        } catch (Exception e) {
            System.out.println("插入记录发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void update() {
        try {
            // 先查询是否存在记录
            String countSql = "SELECT COUNT(*) FROM t_user";
            int count = jdbcTemplate.queryForObject(countSql, Integer.class);
            
            if (count > 0) {
                String sql = "UPDATE t_user SET name = ? WHERE id = (SELECT id FROM t_user LIMIT 1)";
                Object[] params = {"更新后的用户"};
                int rows = jdbcTemplate.update(sql, params);
                System.out.println("更新了 " + rows + " 条记录");
            } else {
                System.out.println("表中没有数据可更新");
            }
        } catch (Exception e) {
            System.out.println("更新记录发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void delete() {
        try {
            // 先查询是否存在记录
            String countSql = "SELECT COUNT(*) FROM t_user";
            int count = jdbcTemplate.queryForObject(countSql, Integer.class);
            
            if (count > 0) {
                String sql = "DELETE FROM t_user WHERE id = (SELECT id FROM t_user LIMIT 1)";
                int rows = jdbcTemplate.update(sql);
                System.out.println("删除了 " + rows + " 条记录");
            } else {
                System.out.println("表中没有数据可删除");
            }
        } catch (Exception e) {
            System.out.println("删除记录发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
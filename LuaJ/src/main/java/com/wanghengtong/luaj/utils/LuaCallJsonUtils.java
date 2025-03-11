package com.wanghengtong.luaj.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wanght
 */
public class LuaCallJsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));

    // 美化输出方法
    public static String encodePretty(LuaValue table) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(LuaCallJsonUtils.convert(table));
        } catch (JsonProcessingException e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }


    // Lua表转JSON字符串
    public static String encode(LuaValue table) {
        try {
            return OBJECT_MAPPER.writeValueAsString(convert(table));
        } catch (JsonProcessingException e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    // JSON字符串转Lua表
    public static LuaValue decode(String json) {
        try {
            Map<?, ?> javaMap = OBJECT_MAPPER.readValue(json, Map.class);
            LuaTable luaTable = LuaValue.tableOf();
            javaMap.forEach((k, v) -> luaTable.set(k.toString(), LuaValue.valueOf(v.toString())));
            return luaTable;
        } catch (IOException e) {
            return LuaValue.NIL;
        }
    }

    private static Object convert(LuaValue value) {
        if (value.istable()) {
            LuaTable table = value.checktable();
            if (isArray(table)) {
                return convertArray(table);
            } else {
                return convertObject(table);
            }
        }
        return value.checkjstring();
    }

    private static boolean isArray(LuaTable table) {
        LuaValue key = LuaValue.NIL;
        while (true) {
            Varargs n = table.next(key);
            if ((key = n.arg1()).isnil()) {
                break;
            }
            if (!key.isint()) {
                return false;
            }
        }
        return true;
    }

    private static List<Object> convertArray(LuaTable table) {
        List<Object> list = new ArrayList<>();
        for (int i = 1; ; i++) {
            LuaValue val = table.get(i);
            if (val.isnil()) {
                break;
            }
            list.add(convert(val));
        }
        return list;
    }

    private static Map<String, Object> convertObject(LuaTable table) {
        Map<String, Object> map = new HashMap<>();
        LuaValue key = LuaValue.NIL;
        while (true) {
            Varargs n = table.next(key);
            if ((key = n.arg1()).isnil()) {
                break;
            }
            map.put(key.checkjstring(), convert(n.arg(2)));
        }
        return map;
    }

}

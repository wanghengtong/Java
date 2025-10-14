package com.wanghengtong.luaj.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.OneArgFunction;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author wanght
 */
public class LuaCallJavaUtils {

    private abstract static class CommonFunction extends LibFunction {
        protected static final Charset UTF_8 = StandardCharsets.UTF_8;

        protected Varargs handleException(Exception e) {
            return LuaValue.varargsOf(LuaValue.NIL,
                    LuaValue.valueOf(e.getClass().getSimpleName() + ": " + e.getMessage()));
        }
    }

    public static class Base64EncodeFunction extends CommonFunction {
        private static final Base64.Encoder ENCODER = Base64.getEncoder();

        @Override
        public LuaValue call(LuaValue arg) {
            try {
                if (!arg.isstring()) {
                    return (LuaValue) LuaValue.varargsOf(LuaValue.NIL,
                            LuaValue.valueOf("Expected string argument"));
                }

                String input = arg.checkjstring();
                if (input.isEmpty()) {
                    return LuaValue.NIL;
                }

                byte[] inputBytes = input.getBytes(UTF_8);
                return LuaValue.valueOf(ENCODER.encodeToString(inputBytes));
            } catch (Exception e) {
                return (LuaValue) handleException(e);
            }
        }
    }

    public static class Base64DecodeFunction extends CommonFunction {
        private static final Base64.Decoder DECODER = Base64.getDecoder();

        @Override
        public LuaValue call(LuaValue arg) {
            try {
                if (!arg.isstring()) {
                    return (LuaValue) LuaValue.varargsOf(LuaValue.NIL,
                            LuaValue.valueOf("Expected string argument"));
                }

                String input = arg.checkjstring();
                if (input.isEmpty()) {
                    return LuaValue.NIL;
                }

                byte[] decodedBytes = DECODER.decode(input);
                return LuaValue.valueOf(new String(decodedBytes, UTF_8));
            } catch (IllegalArgumentException e) {
                return (LuaValue) LuaValue.varargsOf(LuaValue.NIL,
                        LuaValue.valueOf("Invalid Base64 input format"));
            } catch (Exception e) {
                return (LuaValue) handleException(e);
            }
        }
    }

    public static class JsonEncodeFunction extends CommonFunction {
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public LuaValue call(LuaValue arg) {
            try {
                Object javaObj = convertLuaValue(arg);
                String json = objectMapper.writeValueAsString(javaObj);
                return LuaValue.valueOf(json);
            } catch (JsonProcessingException e) {
                return (LuaValue) handleException(e);
            }
        }

        private Object convertLuaValue(LuaValue value) {
            if (value.istable()) {
                return convertLuaTable(value.checktable());
            } else if (value.isstring()) {
                return value.tojstring();
            } else if (value.isnumber()) {
                return value.isint() ? value.toint() : value.todouble();
            } else if (value.isboolean()) {
                return value.toboolean();
            }
            return null;
        }

        private Object convertLuaTable(LuaTable table) {
            Map<String, Object> map = new LinkedHashMap<>();
            List<Object> list = new ArrayList<>();

            // 处理数组部分（连续数字索引）
            int maxArrayIndex = 0;
            LuaValue key = LuaValue.NIL;
            while (true) {
                Varargs n = table.next(key);
                if ((key = n.arg1()).isnil()) {
                    break;
                }
                LuaValue val = n.arg(2);

                if (key.isint()) {
                    int idx = key.toint();
                    if (idx == maxArrayIndex + 1) {
                        list.add(convertLuaValue(val));
                        maxArrayIndex = idx;
                    } else {
                        map.put(key.tojstring(), convertLuaValue(val));
                    }
                } else {
                    map.put(key.tojstring(), convertLuaValue(val));
                }
            }

            return list.isEmpty() ? map : list;
        }
    }

    public static class Md5HexFunction extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            return LuaValue.valueOf(DigestUtils.md5Hex(arg.checkjstring()));
        }
    }

    public static class LogFunction extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            System.out.println(arg.checkjstring());
            return LuaValue.valueOf("");
        }
    }

}
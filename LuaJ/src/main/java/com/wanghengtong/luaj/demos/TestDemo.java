package com.wanghengtong.luaj.demos;

import com.wanghengtong.luaj.utils.LuaCallJsonUtils;
import com.wanghengtong.luaj.utils.LuaCallJavaUtils;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 * @author wanght
 */
public class TestDemo {

    private static LuaTable createJavaUtils() {
        LuaTable javaUtils = new LuaTable();
        javaUtils.set("base64Encode", new LuaCallJavaUtils.Base64EncodeFunction());
        javaUtils.set("base64Decode", new LuaCallJavaUtils.Base64DecodeFunction());
        javaUtils.set("md5Hex", new LuaCallJavaUtils.Md5HexFunction());

        javaUtils.set("luaTableToJson",new LuaCallJavaUtils.JsonEncodeFunction());
        return javaUtils;
    }

    public static void main(String[] args) {
        Globals globals = null;
        try {
            globals = JsePlatform.standardGlobals();
            globals.set("java", createJavaUtils());
            globals.set("json", CoerceJavaToLua.coerce(new LuaCallJsonUtils()));
            globals.load(new Bit32Lib());
            LuaValue luaValue = globals.loadfile("/lua/script.lua");
            if (luaValue.isnil()) {
                System.err.println("Failed to load Lua script");
            } else {
                luaValue.call();
            }
        } catch (LuaError e) {
            System.err.println("Lua execution error: " + e.getMessage());
        } finally {
            if (globals != null) {
                globals.STDOUT.close();
            }
        }
    }

}

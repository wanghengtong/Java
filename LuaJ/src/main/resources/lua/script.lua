print('Hello, World!')
print("转换大写：", string.upper('abc'))

-- 时间操作
print(os.date())  --> 输出类似 Mon Mar  3 20:15:30 2025
print("当前时间：", os.date('%Y-%m-%d %H:%M:%S', os.time()))  --> 2025-03-03 20:15:30

-- 获取当前时间戳
local localTime = os.date("*t")
local timestamp = os.time(localTime)
print("时间戳：", timestamp)

local currentTime = os.time()
local oneHourAgo = currentTime - 3600
local currentFormattedTime = os.date("%Y-%m-%d %H:%M:%S", currentTime)
print("当前时间: " .. currentFormattedTime)
local oneHourAgoFormattedTime = os.date("%Y-%m-%d %H:%M:%S", oneHourAgo)
print("一个小时前的时间: " .. oneHourAgoFormattedTime)

-- 调用 Java 工具方法
local encoded = java.base64Encode("Hello, World!")
print("base64Encode：", encoded)

local decoded = java.base64Decode(encoded)
print("base64Decode：", decoded)

local md5Hex = java.md5Hex("Hello, World!")
print("md5Hex：", md5Hex)

-- JSON操作
local data = {
    name = "John",
    age = 30,
    city = "New York"
}

local jsonResult = java.luaTableToJson(data)
print("luaTableToJson：", jsonResult)

local jsonEncode = json:encode(data)
print("json:encode：", jsonEncode)

local table = json:decode(jsonEncode)
print("json:decode.name：", table.name)
print("json:decode.age：", table.age)
print("json:decode.city：", table.city)

local params = {
    name = "John",
    age = 30,
    city = "New York",
    data = json:encode(data)
}
local table1 = json:decode(json:encode(params))
print("json:decode.name：",table1.name)
local getData = table1.data
print("json:decode.data：",getData)
print("json:decode.data.name：",json:decode(getData).name)




package com.zxy.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
//ctrl+shift+T 单元测试
public class CommUtils {
    private static final Gson GSON = new GsonBuilder().create();
    //加载配置文件，fileName是要加载的文件名称
    public static Properties loadProperities(String fileName){
        Properties properties = new Properties();
        //获取该类的类加载器，将要加载的文件转变为流
        InputStream in = CommUtils.class.getClassLoader().getResourceAsStream(fileName);
        try {
            //加载这个文件流
            properties.load(in);
        } catch (IOException e) {
            return null;
        }
        return properties;
    }
    //Object-->Json  序列化
    public static String objectToJson(Object obj){
        return GSON.toJson(obj);
    }

    //Json-->Object  反序列化  利用了反射 objClass反序列化的类反射对象
    public static Object jsonToObject(String jsonStr,Class objClass){
        return GSON.fromJson(jsonStr,objClass);
    }

}

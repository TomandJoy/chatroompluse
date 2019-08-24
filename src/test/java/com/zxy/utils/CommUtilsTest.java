package com.zxy.utils;

import com.zxy.client.entity.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

public class CommUtilsTest {

    @Test
    public void loadProperities() {
        String fileName = "datasource.properties";
        Properties properties = CommUtils.loadProperities(fileName);
        Assert.assertNotNull(properties);

    }


    @Test
    public void objectToJson() {
        User user = new User();
        user.setUserName("test");
        user.setPassword("123");
        user.setBrief("帅");
        Set<String> strings = new HashSet<>();
        strings.add("test2");
        strings.add("test3");
        strings.add("test4");
        user.setUserNames(strings);
        String str = CommUtils.objectToJson(user);
        System.out.println(str);
    }

    @Test
    public void jsonToObject() {
        String jsonStr = "{\"userName\":\"test\",\"password\":\"123\",\"brief\":\"帅\",\"userNames\":[\"test4\",\"test2\",\"test3\"]}";
        User user = (User) CommUtils.jsonToObject(jsonStr,User.class);
        System.out.println(user.getUserNames());
    }
}
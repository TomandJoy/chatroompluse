package com.zxy.client.dao;

import com.zxy.client.entity.User;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLOutput;

import static org.junit.Assert.*;

public class AccountTest {
    Account account = new Account();
    @Test
    public void userReg(){
      User user = new User();
      user.setUserName("小明");
      user.setPassword("123456");
      user.setBrief("NO.1");
      boolean flag = account.userReg(user);
        Assert.assertTrue(flag);
    }
    @Test
    public void userLogin(){
        String userName = "小明";
        String password = "123456";
        User user = account.userLogin(userName,password);
        System.out.println(user);
        Assert.assertNotNull(user );

    }


}
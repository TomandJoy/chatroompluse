package com.zxy.client.entity;

import lombok.Data;

import java.util.Set;

//描述表的类
@Data
public class User {
    private Integer id;
    private String userName;
    private String password;
    private String brief;
    private Set<String> userNames;
}

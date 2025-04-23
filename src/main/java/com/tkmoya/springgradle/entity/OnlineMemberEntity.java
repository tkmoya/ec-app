package com.tkmoya.springgradle.entity;

import lombok.Data;

import java.sql.Date;

@Data
public class OnlineMemberEntity {
    private int memberNo;
    private String password;
    private String name;
    private int age;
    private String sex;
    private String zip;
    private String address;
    private String tel;
    private Date registerDate;
    private String deleteFlg;
    private Date lastUpdDate;
}

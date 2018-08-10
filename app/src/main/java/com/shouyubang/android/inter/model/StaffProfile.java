package com.shouyubang.android.inter.model;

import java.util.UUID;

/**
 * Created by KunLiu on 2015/12/13.
 */
public class StaffProfile {

    private String id;
    private String phone;
    private String avatarUrl;
    private String nickname;
    private int gender;
    private String age;
    private String city;
    private String industry;
    private String company;
    private String profession;
    private int online;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return "StaffProfile{" +
                "id='" + id + '\'' +
                ", phone='" + phone + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", nickname='" + nickname + '\'' +
                ", gender=" + gender +
                ", age='" + age + '\'' +
                ", city='" + city + '\'' +
                ", industry='" + industry + '\'' +
                ", company='" + company + '\'' +
                ", profession='" + profession + '\'' +
                ", online=" + online +
                '}';
    }
}



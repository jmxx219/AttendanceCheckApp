package com.example.attendancecheckapp.data;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private String id;

    @SerializedName("account")
    private String account;

    @SerializedName("name")
    private String name;

    @SerializedName("school_number")
    private String school_number;

    @SerializedName("user_type")
    private String user_type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchool_number() {
        return school_number;
    }

    public void setSchool_number(String school_number) {
        this.school_number = school_number;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }
}

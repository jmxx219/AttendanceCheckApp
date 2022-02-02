package com.example.attendancecheckapp.data;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("account")
    private String account;

    @SerializedName("password")
    private String password;

    public LoginRequest(String account, String password){
        this.account = account;
        this.password = password;
    }

    public String getAccount() {
        return account;
    }
    public void setAccount(String s){
        account = s;
    }

    public String getPassword() { return password; }
    public void setPassword(String s){ password = s; }

}

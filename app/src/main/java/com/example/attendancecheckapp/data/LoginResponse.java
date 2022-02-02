package com.example.attendancecheckapp.data;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("token")
    public String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

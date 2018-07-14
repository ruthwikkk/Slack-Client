package com.ruthwikwarrier.slackclient.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    private String userId;

    @SerializedName("real_name")
    private String fullName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}

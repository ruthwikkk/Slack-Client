package com.ruthwikwarrier.slackclient.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListUsersResponse {

    @SerializedName("ok")
    private boolean status;

    @SerializedName("members")
    private List<User> userList = null;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}

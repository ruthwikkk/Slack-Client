package com.ruthwikwarrier.slackclient.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChannelHistory {

    @SerializedName("ok")
    private boolean status;

    @SerializedName("messages")
    private List<Message> messageList = null;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }
}

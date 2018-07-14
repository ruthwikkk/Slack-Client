package com.ruthwikwarrier.slackclient.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListChannelResponse {

    @SerializedName("ok")
    private boolean status;

    @SerializedName("channels")
    private List<Channel> channelList = null;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }
}

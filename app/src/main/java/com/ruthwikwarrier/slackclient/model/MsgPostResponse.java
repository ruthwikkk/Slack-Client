package com.ruthwikwarrier.slackclient.model;

import com.google.gson.annotations.SerializedName;

public class MsgPostResponse {

    @SerializedName("ok")
    private boolean status;

    @SerializedName("error")
    private String errorMessage;

    @SerializedName("channel")
    private String channelID;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }
}

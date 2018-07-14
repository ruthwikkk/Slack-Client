package com.ruthwikwarrier.slackclient.model;

import com.google.gson.annotations.SerializedName;

public class RTMConnectResponse {

    @SerializedName("ok")
    private boolean status;

    @SerializedName("url")
    private String url;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

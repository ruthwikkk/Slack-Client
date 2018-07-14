package com.ruthwikwarrier.slackclient.model;

import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("user")
    private String author;

    @SerializedName("text")
    private String text;

    private String channelName;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}

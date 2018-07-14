package com.ruthwikwarrier.slackclient.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Channel implements Parcelable{

    @SerializedName("id")
    private String channelID;

    @SerializedName("name")
    private String channelName;

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    protected Channel(Parcel in) {
        channelID = in.readString();
        channelName = in.readString();
    }

    public Channel(String id, String name) {
        this.channelID = id;
        this.channelName = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(channelID);
        dest.writeString(channelName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Channel> CREATOR = new Parcelable.Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel in) {
            return new Channel(in);
        }

        @Override
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };
}

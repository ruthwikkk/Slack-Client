package com.ruthwikwarrier.slackclient.retrofit;

import com.ruthwikwarrier.slackclient.data.Web;
import com.ruthwikwarrier.slackclient.model.ChannelHistory;
import com.ruthwikwarrier.slackclient.model.ListChannelResponse;
import com.ruthwikwarrier.slackclient.model.ListUsersResponse;
import com.ruthwikwarrier.slackclient.model.RTMConnectResponse;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIInterface {

    @POST(Web.rtmConnect)
    @FormUrlEncoded
    Call<RTMConnectResponse> connectRTM(@Field("token") String token);

    @POST(Web.getChannelList)
    @FormUrlEncoded
    Call<ListChannelResponse> getChannelList(@Field("token") String token, @Field("limit") String limit);

    @POST(Web.getUserList)
    @FormUrlEncoded
    Call<ListUsersResponse> getUserList(@Field("token") String token);

    @POST(Web.getChannelHistory)
    @FormUrlEncoded
    Call<ChannelHistory> getMessages(@Field("token") String token, @Field("channel") String channel, @Field("count") String count);

    /*@POST(Web.postMessageChannel)
    @FormUrlEncoded
    Call<MsgPostResponse> postMessage(@Field("token") String token, @Field("channel") String channel, @Field("text") String text, @Field("as_user") boolean isUser);
*/
}

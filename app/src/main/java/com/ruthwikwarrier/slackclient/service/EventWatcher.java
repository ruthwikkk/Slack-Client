package com.ruthwikwarrier.slackclient.service;

import android.support.annotation.Nullable;
import android.util.Log;

import com.ruthwikwarrier.slackclient.data.StringData;
import com.ruthwikwarrier.slackclient.model.Message;
import com.ruthwikwarrier.slackclient.model.RTMConnectResponse;
import com.ruthwikwarrier.slackclient.retrofit.APIClient;
import com.ruthwikwarrier.slackclient.retrofit.APIInterface;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.work.Worker;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventWatcher extends Worker {

    String TAG = "EventWatcher";
    String url = "";
    int socket_id = 1;
    WebSocket webSocket;
    APIInterface apiInterface;

    @Override
    public Worker.Result doWork()  {

        Log.e(TAG,"Running..");
        apiInterface = APIClient.getClient().create(APIInterface.class);
        connectToRTM(StringData.slackToken);


        return Result.SUCCESS;
    }

    private void connectToRTM(String token){

        Call<RTMConnectResponse> call = apiInterface.connectRTM(token);
        call.enqueue(rtmCallback);

    }

    Callback<RTMConnectResponse> rtmCallback = new Callback<RTMConnectResponse>() {
        @Override
        public void onResponse(Call<RTMConnectResponse> call, Response<RTMConnectResponse> response) {

            RTMConnectResponse rtmConnectResponse = response.body();
            Log.e("WrokManager", "RTM Call => URL:" + rtmConnectResponse.getUrl());
            url = rtmConnectResponse.getUrl();
            initSocket();
        }

        @Override
        public void onFailure(Call<RTMConnectResponse> call, Throwable t) {

        }
    };

    public void initSocket() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        webSocket = client.newWebSocket(request, webSocketListener);
        webSocket.request();
    }

    WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            super.onOpen(webSocket, response);
            Log.e(TAG, "Web Socket opened");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            Log.e(TAG, "RTM Event: "+ text);
            /*try {
                JSONObject jsonObject = new JSONObject(text);
                if(jsonObject.getString("type").equals("message")){
                    Log.e(TAG, "RTM Event: New message received");
                    Message message = new Message();
                    message.setText(jsonObject.getString("text"));
                    message.setChannelName(jsonObject.getString("channel"));
                    message.setAuthor(jsonObject.getString("user"));
                    messageList.add(message);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateList();
                        }
                    });

                }

            } catch (JSONException e) {
                Log.e(TAG, "Web Socket JSON exception");
                e.printStackTrace();
            }*/
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            Log.e(TAG, "Web Socket Closed. Reason: "+reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, @Nullable okhttp3.Response response) {
            super.onFailure(webSocket, t, response);
            Log.e(TAG, "Web Socket Failure. Response: "+response);
            webSocket.request();
            t.printStackTrace();
        }
    };
}

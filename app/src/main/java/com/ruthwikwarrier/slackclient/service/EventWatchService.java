package com.ruthwikwarrier.slackclient.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ruthwikwarrier.slackclient.MainActivity;
import com.ruthwikwarrier.slackclient.R;
import com.ruthwikwarrier.slackclient.data.StringData;
import com.ruthwikwarrier.slackclient.model.Message;
import com.ruthwikwarrier.slackclient.model.RTMConnectResponse;
import com.ruthwikwarrier.slackclient.retrofit.APIClient;
import com.ruthwikwarrier.slackclient.retrofit.APIInterface;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventWatchService extends Service {

    public static final String BROADCAST_ACTION_RTM_EVENT = "com.ruthwikwarrier.broadcast_action.RTM";
    public static final String PARAM_RTM_EVENT = "com.ruthwikwarrier.extra.PARAM_RTM_EVENT";
    private static int socket_id = 1;

    static String TAG = "EventWatchService";
    String url = "";
    static WebSocket webSocket;
    APIInterface apiInterface;

    private NotificationManager notificationManager;
    String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
    private boolean pinOnTop = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"onCreate()");
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        startForeground(1, getOreoNotification("Slack Client running", false, NotificationCompat.VISIBILITY_SECRET ));
        apiInterface = APIClient.getClient().create(APIInterface.class);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(TAG,"onStartCommand()");
        connectToRTM(StringData.slackToken);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG," => onDestroy()");
    }

    public static void startCBService(Context context) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(context, EventWatchService.class);
            context.startForegroundService(intent);
        } else {
            Intent intent = new Intent(context, EventWatchService.class);
            context.startService(intent);
        }

    }

    public static void sendMessageToRTM(String msgText, String channelID){

        JSONObject msgJson = new JSONObject();
        try {
            msgJson.put("id", ++socket_id);
            msgJson.put("type", "message");
            msgJson.put("channel", channelID);
            msgJson.put("text", msgText);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webSocket.send(String.valueOf(msgJson));
        Log.e(TAG, "RTM Call => Message sent to server");
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
            Log.e(TAG, "RTM Event");

            try {
                JSONObject jsonObject = new JSONObject(text);
                if(jsonObject.getString("type").equals("message")){
                    Log.e(TAG, "RTM Event: New message received");
                    broadcastRTMEvent(text);
                    notificationManager.notify(2, getOreoNotification("New message", true, NotificationCompat.VISIBILITY_PRIVATE));
                }

            } catch (JSONException e) {
                Log.e(TAG, "Web Socket JSON exception");
                e.printStackTrace();
            }

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

    public void broadcastRTMEvent(String param) {
        Intent intent = new Intent(BROADCAST_ACTION_RTM_EVENT);
        intent.putExtra(PARAM_RTM_EVENT, param);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(getApplicationContext());
        bm.sendBroadcast(intent);
    }

    private Notification getOreoNotification(String title, boolean isCancellable, int visibility){

       Intent openMainIntent = new Intent(this, MainActivity.class);
        openMainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pOpenMainIntent = PendingIntent.getActivity(this, 0, openMainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription("Channel description");
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
        notificationChannel.enableVibration(true);
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setContentIntent(pOpenMainIntent)
                .setContentTitle(title)
                .setOngoing(pinOnTop)
                .setAutoCancel(isCancellable)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setVisibility(visibility);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Notification notification = builder.build();
        return notification;
    }
}

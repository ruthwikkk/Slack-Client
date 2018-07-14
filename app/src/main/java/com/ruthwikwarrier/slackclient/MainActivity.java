package com.ruthwikwarrier.slackclient;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ruthwikwarrier.slackclient.adapters.ChatListAdapter;
import com.ruthwikwarrier.slackclient.data.StringData;
import com.ruthwikwarrier.slackclient.fragments.ChannelSelectDialog;
import com.ruthwikwarrier.slackclient.model.Channel;
import com.ruthwikwarrier.slackclient.model.ChannelHistory;
import com.ruthwikwarrier.slackclient.model.ListChannelResponse;
import com.ruthwikwarrier.slackclient.model.ListUsersResponse;
import com.ruthwikwarrier.slackclient.model.Message;
import com.ruthwikwarrier.slackclient.model.User;
import com.ruthwikwarrier.slackclient.retrofit.APIClient;
import com.ruthwikwarrier.slackclient.retrofit.APIInterface;
import com.ruthwikwarrier.slackclient.service.EventWatchService;
import com.ruthwikwarrier.slackclient.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ChannelSelectDialog.SelectChannelCallback{

    @BindView(R.id.rv_message_main)
    RecyclerView chatListRecyclerView;
    @BindView(R.id.edt_type_main)
    EditText edtNewMsg;
    @BindView(R.id.btn_send_main)
    ImageButton btnSend;


    String TAG = "MainActivity";
    ChatListAdapter chatListAdapter;
    APIInterface apiInterface;
    Context context;
    List<Message> messageList;
    List<Channel> channelList;
    public static final String PARAM_RTM_EVENT = "com.ruthwikwarrier.extra.PARAM_RTM_EVENT";

    String[] currentChannel;
    int webCallIndex = 0;
    HashMap<String, String> userMap;
    HashMap<String, String> channelMap;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;

        init();
        getUsersFromSlack();

        if( !AppUtils.isMyServiceRunning(EventWatchService.class, this))
            EventWatchService.startCBService(context);
        else
            Log.e(TAG,"CBWatch Service already running.");

        IntentFilter filter = new IntentFilter();
        filter.addAction(EventWatchService.BROADCAST_ACTION_RTM_EVENT);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.registerReceiver(mBroadcastReceiver, filter);

    }

    private void init() {

        apiInterface = APIClient.getClient().create(APIInterface.class);
        messageList = new ArrayList<>();
        dialog = new ProgressDialog(this);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtNewMsg.getText().length() > 0){
                    FragmentManager fm = getFragmentManager();
                    ChannelSelectDialog selectDialog = new ChannelSelectDialog();

                    Bundle args = new Bundle();
                    args.putSerializable("Channels", (Serializable) channelList);
                    selectDialog.setArguments(args);
                    selectDialog.show(fm, "");
                }else{
                    AppUtils.showToast(context, "Type something", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void getUsersFromSlack() {

        if(AppUtils.isOnline(this)){
            dialog.setMessage("Loading..");
            dialog.show();
            Call<ListUsersResponse> call = apiInterface.getUserList(StringData.slackToken);
            call.enqueue(userCallback);
        }else{
            AppUtils.showToast(this, "No Internet Connection", Toast.LENGTH_SHORT);
        }

    }


    private void getChannelsFromSlack() {

        if(AppUtils.isOnline(this)){
            Call<ListChannelResponse> call = apiInterface.getChannelList(StringData.slackToken, StringData.channelLimit);
            call.enqueue(channelCallback);
        }else{
            AppUtils.showToast(this, "No Internet Connection", Toast.LENGTH_SHORT);
        }

    }

    private void getMessagesFromSlack(String channelId) {

        Log.e(TAG," Calling History of "+currentChannel+" ");

        if(AppUtils.isOnline(this)){
            Call<ChannelHistory> call = apiInterface.getMessages(StringData.slackToken, channelId, StringData.messageLimit);
            call.enqueue(messageCallback);
        }else{
            AppUtils.showToast(this, "No Internet Connection", Toast.LENGTH_SHORT);
        }

    }

    Callback<ListUsersResponse> userCallback = new Callback<ListUsersResponse>() {
        @Override
        public void onResponse(Call<ListUsersResponse> call, Response<ListUsersResponse> response) {

            ListUsersResponse listUsersResponse = response.body();
            List<User> userList = listUsersResponse.getUserList();
            userMap = new HashMap<>();
            for(User user: userList){
                userMap.put(user.getUserId(), user.getFullName());
            }
            getChannelsFromSlack();

        }

        @Override
        public void onFailure(Call<ListUsersResponse> call, Throwable t) {

        }
    };

    Callback<ListChannelResponse> channelCallback = new Callback<ListChannelResponse>() {
        @Override
        public void onResponse(Call<ListChannelResponse> call, Response<ListChannelResponse> response) {

            ListChannelResponse listChannelResponse = response.body();
            if(listChannelResponse.isStatus()){
                channelList = listChannelResponse.getChannelList();
                Log.e(TAG, "Channel List Size:"+ channelList.size());
                currentChannel = new String[channelList.size()];
                channelMap = new HashMap<>();

                for(int i=0; i< channelList.size(); i++){
                    Channel channel = channelList.get(i);
                    currentChannel[i] = channel.getChannelID();
                    channelMap.put(channel.getChannelID(), channel.getChannelName());
                    Log.e(TAG, "Current Channel: "+ currentChannel[webCallIndex]);
                    getMessagesFromSlack(channel.getChannelID());
                }

            }
        }

        @Override
        public void onFailure(Call<ListChannelResponse> call, Throwable t) {

        }
    };

    Callback<ChannelHistory> messageCallback = new Callback<ChannelHistory>() {
        @Override
        public void onResponse(Call<ChannelHistory> call, Response<ChannelHistory> response) {

            webCallIndex++;
            ChannelHistory channelHistory = response.body();
            List<Message> list = channelHistory.getMessageList();
            for(Message msg: list){
                Log.e(TAG, "Setting channel name to message => Name: "+ currentChannel[webCallIndex-1]);
                msg.setChannelName(currentChannel[webCallIndex-1]);
                messageList.add(msg);
            }
            if(webCallIndex == channelList.size()){
                Log.e(TAG, "Completed group calls...setting adapter");
                setAdapter();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }

        @Override
        public void onFailure(Call<ChannelHistory> call, Throwable t) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    };


    private void setAdapter() {

        Collections.reverse(messageList);
        chatListAdapter = new ChatListAdapter(this, messageList, userMap, channelMap);
        chatListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatListRecyclerView.setAdapter(chatListAdapter);
        chatListRecyclerView.smoothScrollToPosition(chatListAdapter.getItemCount());

    }

    private void updateList(){
        chatListAdapter.notifyDataSetChanged();
        chatListRecyclerView.smoothScrollToPosition(chatListAdapter.getItemCount());
    }

    @Override
    public void onSelect(final String channelID) {

        final String msgText = edtNewMsg.getText().toString().trim();
        edtNewMsg.getText().clear();
        if (AppUtils.isOnline(context)) {

            EventWatchService.sendMessageToRTM(msgText, channelID);
            Message message = new Message();
            message.setText(msgText);
            message.setAuthor("You");
            message.setChannelName(channelID);
            messageList.add(message);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateList();
                }
            });

        } else {
            AppUtils.showToast(context, "No Internet Connection", Toast.LENGTH_SHORT);

        }
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(EventWatchService.BROADCAST_ACTION_RTM_EVENT)) {
                final String text = intent.getStringExtra(PARAM_RTM_EVENT);
                // do something
                Log.e(TAG, "Data From Service: "+text);
                try {
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
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
        /*WorkManager workManager = WorkManager.getInstance();
        //PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(EventWatcher.class, 1000, TimeUnit.MILLISECONDS).build();
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(EventWatcher.class).build();
        workManager.enqueue(oneTimeWorkRequest);*/

    }
}

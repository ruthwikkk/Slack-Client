package com.ruthwikwarrier.slackclient.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruthwikwarrier.slackclient.R;
import com.ruthwikwarrier.slackclient.model.Message;
import com.ruthwikwarrier.slackclient.viewholders.ChatListViewHolder;

import java.util.HashMap;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListViewHolder> {

    List<Message> messageList;
    Context context;
    HashMap<String, String> userMap;
    HashMap<String, String> channelMap;

    public ChatListAdapter(Context con, List<Message> list, HashMap<String, String> uMmap, HashMap<String, String> cMap) {

        this.messageList = list;
        this.context = con;
        this.userMap = uMmap;
        this.channelMap = cMap;

    }

    @Override
    public ChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_row, parent, false);
        ChatListViewHolder viewHolder = new ChatListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChatListViewHolder holder, final int position) {

        Message chatMessage = messageList.get(position);
        holder.textChat.setText(chatMessage.getText());
        holder.textAuthor.setText(userMap.get(chatMessage.getAuthor()));
        holder.textChannel.setText("#"+channelMap.get(chatMessage.getChannelName()));
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }
}

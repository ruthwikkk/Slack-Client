package com.ruthwikwarrier.slackclient.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ruthwikwarrier.slackclient.R;

public class ChatListViewHolder extends RecyclerView.ViewHolder {

    public TextView textChat;
    public TextView textAuthor;
    public TextView textChannel;

    public ChatListViewHolder(View itemView) {
        super(itemView);

        this.textChat = itemView.findViewById(R.id.text_chat_text);
        this.textAuthor = itemView.findViewById(R.id.text_chat_author);
        this.textChannel = itemView.findViewById(R.id.text_chat_channel);
    }
}

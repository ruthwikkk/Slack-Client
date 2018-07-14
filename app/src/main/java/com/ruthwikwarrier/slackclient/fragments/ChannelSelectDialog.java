package com.ruthwikwarrier.slackclient.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ruthwikwarrier.slackclient.R;
import com.ruthwikwarrier.slackclient.model.Channel;

import java.util.ArrayList;

public class ChannelSelectDialog extends DialogFragment {

    private SelectChannelCallback selectChannelCallback;

    public interface SelectChannelCallback {
        void onSelect(String channelID);

    }

    LinearLayout btnLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat_select_dialog, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnLayout = rootView.findViewById(R.id.ll_selectdlg_channellist);

        Bundle b = getArguments();
        ArrayList<Channel> channelList = (ArrayList<Channel>) b.getSerializable("Channels");

        try {
            selectChannelCallback = (SelectChannelCallback) getActivity();
        } catch (Exception e) {
            throw new ClassCastException("Calling Fragment must implement OfferValidationCallback");
        }

        for(Channel channel:channelList){
            addButton(channel, btnLayout);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
//        Window window = getDialog().getWindow();
//        if(window == null) return;
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.width = (int) pxFromDp(getActivity(), getResources().getDimensionPixelSize(R.dimen.offer_dialog_width));
//        params.height = (int) pxFromDp(getActivity(), getResources().getDimensionPixelSize(R.dimen.offer_dialog_hieght));
//        window.setAttributes(params);

        // int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        // int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
    }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    private void addButton(final Channel channel, LinearLayout parent){

        Button myButton = new Button(getContext());
        myButton.setText("#"+channel.getChannelName());

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        myButton.setAllCaps(false);
        parent.addView(myButton, lp);

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Dialog"," Button Clicked :"+ channel.getChannelName());
                selectChannelCallback.onSelect(channel.getChannelID());
                dismiss();
            }
        });
    }
}

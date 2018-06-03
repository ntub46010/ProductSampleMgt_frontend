package com.vincent.psm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vincent.psm.R;
import com.vincent.psm.data.Notification;

import java.util.ArrayList;

public class NotificationListAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private ArrayList<Notification> notifications;

    public NotificationListAdapter (Context context, ArrayList<Notification> notifications) {
        layoutInflater = LayoutInflater.from(context);
        this.notifications = notifications;
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null)
            view = layoutInflater.inflate(R.layout.lst_notification, parent, false);

        TextView txtTitle = view.findViewById(R.id.txtTitle);
        TextView txtContent = view.findViewById(R.id.txtContent);
        TextView txtTime = view.findViewById(R.id.txtCreateTime);

        Notification notify = (Notification) getItem(position);
        txtTitle.setText(notify.getTitle());
        txtContent.setText(notify.getContent());
        txtTime.setText(notify.getCreateTime().substring(5, notify.getCreateTime().length()).replace("-", "/"));

        return view;
    }
}

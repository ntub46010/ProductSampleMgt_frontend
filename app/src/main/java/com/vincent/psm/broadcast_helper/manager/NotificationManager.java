package com.vincent.psm.broadcast_helper.manager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.vincent.psm.notification.NotificationActivity;
import com.vincent.psm.R;

import static com.vincent.psm.data.DataHelper.KEY_ACCOUNT;

public class NotificationManager {
    public static final int NOTIFICATION_ID = -Integer.MAX_VALUE;
    private static NotificationManager INSTANCE = null;

    private NotificationManager() {

    }

    public synchronized static NotificationManager getInstance() {
        if (INSTANCE == null) {
            return new NotificationManager();
        }
        return INSTANCE;
    }

    public void generateNotification(Context context, Bitmap bitmap, String title, String message) {
        //onResourceReady
        Notification notification = createNotification(context, bitmap, title, message);
        notifyNotification(context, notification);
    }

    public void generateNotification(Context context, String title, String message) {
        //onLoadFailed
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher); //通知大圖示
        Notification notification = createNotification(context, bitmap, title, message);
        notifyNotification(context, notification);
    }

    private Notification createNotification(Context context, Bitmap bitmap, String title, String message) {
        //取得最後登入的帳號，讓點擊後能開啟該帳號的通知
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_filename), Context.MODE_PRIVATE);
        Intent it = new Intent(context.getApplicationContext(), NotificationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ACCOUNT, sp.getString(context.getString(R.string.sp_login_user), ""));
        it.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context.getApplicationContext(),
                0,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Notification notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setLargeIcon(bitmap)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();
        notification.flags |= Intent.FLAG_ACTIVITY_SINGLE_TOP;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        return notification;
    }

    private void notifyNotification(Context context, Notification notification) {
        if (notification != null) {
            android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }
}

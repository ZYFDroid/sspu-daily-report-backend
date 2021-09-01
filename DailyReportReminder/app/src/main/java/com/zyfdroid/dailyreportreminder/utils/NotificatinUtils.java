package com.zyfdroid.dailyreportreminder.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.zyfdroid.dailyreportreminder.ui.MainActivity;
import com.zyfdroid.dailyreportreminder.R;

public class NotificatinUtils {

    public static final int notificationId=852898;
    public static final String channelid="remind_v2";
    public static final String channelname="每日一报提醒通知";
    public static void postNoification(Context ctx){
        postNoification(ctx,"每日一报提醒","点击打开上报页面");
    }

    public static void showNotificationSetting(Context ctx){
        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(channelid,channelname, NotificationManager.IMPORTANCE_HIGH);
            channel.setSound( RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());
            channel.setBypassDnd(true);
            channel.setVibrationPattern(vibrate);
            manager.createNotificationChannel(channel);

            Intent i = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            i.putExtra(Settings.EXTRA_APP_PACKAGE,ctx.getPackageName());
            i.putExtra(Settings.EXTRA_CHANNEL_ID,channelid);
            ctx.startActivity(i);
        }else{

            try{
                Intent i = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                i.putExtra(Intent.EXTRA_PACKAGE_NAME,ctx.getPackageName());
                ctx.startActivity(i);
            }catch (Exception ex){
                Toast.makeText(ctx, "打开设置失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static final long[] vibrate=new long[]{0,200,200,200,500,1000,500,1000,500,1000,250};
    public static void postNoification(Context ctx,String title,String msg){
        Notification.Builder nb;
        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(channelid,channelname, NotificationManager.IMPORTANCE_HIGH);
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_ALARM).setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT).build());
            channel.setBypassDnd(true);
            channel.setVibrationPattern(vibrate);
            manager.createNotificationChannel(channel);
            nb= new Notification.Builder(ctx,channelid);
        }else{
            nb = new Notification.Builder(ctx);
            nb.setVibrate(vibrate);
            nb.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), AudioManager.STREAM_ALARM);

        }
        PendingIntent pintent = PendingIntent.getActivity(ctx,0,new Intent(ctx, MainActivity.class),0);
        nb.setContentIntent(pintent);
        nb.setFullScreenIntent(pintent,true);
        nb.setSmallIcon(R.drawable.ic_launcher_foreground);
        nb.setLargeIcon(Icon.createWithResource(ctx,R.mipmap.ic_launcher));
        nb.setContentTitle(title);
        nb.setOngoing(true);

        nb.setAutoCancel(false);
        nb.setContentText(msg);
        manager.notify(notificationId,nb.build());
    }

    public static void removeNotification(Context ctx){
        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }

    public static void OnReportCompleted(Context ctx){
        removeNotification(ctx);
    }

}

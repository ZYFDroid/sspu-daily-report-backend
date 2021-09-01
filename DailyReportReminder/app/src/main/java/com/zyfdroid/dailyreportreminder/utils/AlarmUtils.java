package com.zyfdroid.dailyreportreminder.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.zyfdroid.dailyreportreminder.receiver.AlarmReceiver;
import com.zyfdroid.dailyreportreminder.ui.PerformActivity;

import java.util.Date;

public class AlarmUtils {
    public static final int REQUEST_ID=2004;
    public static final int REQUEST_ID2=2005;
    public static void setAlarm(Context ctx, Date date){
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(ctx.getApplicationContext(), AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(ctx.getApplicationContext(),REQUEST_ID,i,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(date.getTime(),pi),pi);
    }

    public static void cancelAlarm(Context ctx){
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        try {
            alarmManager.cancel(alarmManager.getNextAlarmClock().getShowIntent());
        }catch (Exception ignored){
        }
    }

    public static void setAutoReport(Context ctx){
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(ctx.getApplicationContext(), PerformActivity.class);
        i.putExtra("temp",String.valueOf(SpUtils.on(ctx).getInt(SpUtils.TEMPERATURE,366) / 10f));
        i.putExtra("auto",true);
        i.putExtra("un",SpUtils.on(ctx).getString(SpUtils.USERNAME,""));
        i.putExtra("pw",SpUtils.on(ctx).getString(SpUtils.PASSWORD,""));
        i.putExtra("attempt",true);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(ctx.getApplicationContext(),REQUEST_ID2,i,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(System.currentTimeMillis()+60000,pi),pi);
    }

}

package com.zyfdroid.dailyreportreminder.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtils {
    public static final String TEMPERATURE="temp";
    public static final String USERNAME="un";
    public static final String PASSWORD="pw";
    public static final String FIRST_RUN ="nodata";

    public static final String DAY_REPORTED="dayreported";
    public static final String ENABLE_ALARM="enablealarm";
    public static final String TRY_AUTO_REPORT="tryreport";
    public static final String ALARM_WITH_SOUND="alarmsound";
    public static final String TUTORIAL_READ="tutorialread";

    public static SharedPreferences on(Context ctx){
        return ctx.getSharedPreferences("settings",Context.MODE_PRIVATE);
    }

    public static boolean shouldSetAlarm(Context ctx){
        boolean b1 =  SpUtils.on(ctx).getBoolean(TRY_AUTO_REPORT,false) ,b2= SpUtils.on(ctx).getBoolean(ENABLE_ALARM,false);
        if(b2){
            return true;
        }
        return false;
    }

    public static boolean autoOnly(Context ctx){
        return false;
    }
}

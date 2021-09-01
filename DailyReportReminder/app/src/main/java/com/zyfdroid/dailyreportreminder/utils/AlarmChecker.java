package com.zyfdroid.dailyreportreminder.utils;

import android.content.Context;

import java.util.Date;

public class AlarmChecker {
    public static void checkAlarmClock(Context ctx){
        if(SpUtils.shouldSetAlarm(ctx)){
            Date now = new Date();
            AlarmUtils.setAlarm(ctx,TimeUtils.computeNextTime(SpUtils.on(ctx).getLong(SpUtils.DAY_REPORTED,-1),now,SpUtils.autoOnly(ctx)));
        }
        else{
            AlarmUtils.cancelAlarm(ctx);
        }
    }


}

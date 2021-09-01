package com.zyfdroid.dailyreportreminder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zyfdroid.dailyreportreminder.utils.AlarmChecker;
import com.zyfdroid.dailyreportreminder.utils.AlarmUtils;
import com.zyfdroid.dailyreportreminder.utils.NotificatinUtils;
import com.zyfdroid.dailyreportreminder.utils.SpUtils;
import com.zyfdroid.dailyreportreminder.utils.TimeUtils;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmTest";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w(TAG, "onReceive");
        if(SpUtils.shouldSetAlarm(context)) {
            Date now = new Date();
            if (SpUtils.on(context).getBoolean(SpUtils.ENABLE_ALARM, false)) {
                String[] msg = TimeUtils.computeTimeMessage(now).split("\\|");
                if(SpUtils.on(context).getBoolean(SpUtils.TRY_AUTO_REPORT,false) ==false && TimeUtils.computeAutoTryNecessary(now)){
                    msg[1]="点击打开👆每日一报";
                }
                NotificatinUtils.postNoification(context, msg[0], msg[1]);

                if(SpUtils.on(context).getBoolean(SpUtils.TRY_AUTO_REPORT,false) && TimeUtils.computeAutoTryNecessary(now)){
                    AlarmUtils.setAutoReport(context);
                }
            }
            AlarmChecker.checkAlarmClock(context);
        }
    }
}

package com.zyfdroid.dailyreportreminder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zyfdroid.dailyreportreminder.utils.AlarmChecker;

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmChecker.checkAlarmClock(context);
    }
}

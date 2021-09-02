package com.zyfdroid.dailyreportreminder.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.zyfdroid.dailyreportreminder.R;
import com.zyfdroid.dailyreportreminder.utils.AlarmChecker;
import com.zyfdroid.dailyreportreminder.utils.AlarmUtils;
import com.zyfdroid.dailyreportreminder.utils.ConfBean;
import com.zyfdroid.dailyreportreminder.utils.NotificatinUtils;
import com.zyfdroid.dailyreportreminder.utils.SpUtils;
import com.zyfdroid.dailyreportreminder.utils.TimeUtils;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationActivity extends Activity {

    public String eula="防疫工作是每个人都需要认真完成的工作。本程序的目的不是自动完成每日一报，因此使用 尝试自动填报 功能需要注意一下几点。\n" +
            "继续启用 自动填报 功能则表示您已充分理解并下面几点\n" +
            "1. 自动填报只适用于特殊情况下（例如参与考试时上交手机后）\n" +
            "2. 自动填报仅会在开启提醒之后才生效，仅打开 尝试自动填报 不会有效\n" +
            "3. 自动填报每月只有5次机会，不可累计，每月1日零点清零。机会用尽后自动填报将不生效\n" +
            "4. 自动填报可能会因为网络，电量，后台设置等原因失效。您需要自行承担风险\n" +
            "5. 自动填报可能会因为相关方的要求停止使用";

    public void markReported(View view) {
        SpUtils.on(this).edit().putLong(SpUtils.DAY_REPORTED, TimeUtils.getDayStamp(new Date())).apply();
        NotificatinUtils.removeNotification(this);
        Toast.makeText(this, "已设置明天开始报", Toast.LENGTH_SHORT).show();
        AlarmChecker.checkAlarmClock(this);
        updateText();
    }

    void updateText(){
        if(!SpUtils.shouldSetAlarm(this)){
            ((TextView)findViewById(R.id.txtNextNotification)).setText("未设置提醒");
        }
        else{
            ((TextView)findViewById(R.id.txtNextNotification))
                    .setText("下次提醒："+
                            TimeUtils.sdf.format(
                                    TimeUtils.computeNextTime(SpUtils.on(this).getLong(SpUtils.DAY_REPORTED,-1),new Date(),SpUtils.autoOnly(this))));
       }
    }

    public void onAutoWarningMessage(View view) {
        if(((Switch)view).isChecked()){
            new AlertDialog.Builder(this).setTitle("免责声明").setMessage(eula).setPositiveButton(android.R.string.ok,null).create().show();
        }
    }

    static class SwitchBinder{
        int ctlid;
        String key;
        boolean defaultValue = false;

        public SwitchBinder(int ctlid, String key, boolean defaultValue) {
            this.ctlid = ctlid;
            this.key = key;
            this.defaultValue = defaultValue;
        }
    }

    List<SwitchBinder> settingBinders = new ArrayList<>();

    public void loadSettings(){
        CompoundButton.OnCheckedChangeListener chks = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveSettings();
                updateText();
            }
        };
        for (SwitchBinder sb : settingBinders) {
            CompoundButton cb = findViewById(sb.ctlid);
            cb.setChecked(SpUtils.on(this).getBoolean(sb.key,sb.defaultValue));
            cb.setOnCheckedChangeListener(chks);
        }
    }

    public void saveSettings(){
        for (SwitchBinder sb : settingBinders) {
            CompoundButton cb = findViewById(sb.ctlid);
            SpUtils.on(this).edit().putBoolean(sb.key,cb.isChecked()).apply();
        }
        AlarmChecker.checkAlarmClock(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initUi();

        settingBinders.add(new SwitchBinder(R.id.chkEnableNotification, SpUtils.ENABLE_ALARM,false));
        settingBinders.add(new SwitchBinder(R.id.chkPlaySound,SpUtils.ALARM_WITH_SOUND,false));
        settingBinders.add(new SwitchBinder(R.id.chkTryAuto,SpUtils.TRY_AUTO_REPORT,false));
        loadSettings();
        ConfBean confBean = ConfBean.getConf(this);
        if(!confBean.getAllowauto()){
            findViewById(R.id.chkTryAuto).setEnabled(false);
            ((CompoundButton)findViewById(R.id.chkTryAuto)).setEnabled(false);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateText();
    }

    @Override
    public void onBackPressed() {
        saveSettings();
        super.onBackPressed();
    }

    Button testTurnOffNotification = null;
    Button testNotification = null;
    Button allowWhenDnd = null;
    Switch chkTryAuto = null;
    Switch chkPlaySound = null;
    Switch chkEnableNotification = null;

    void initUi(){
        testTurnOffNotification = (Button)findViewById(R.id.testTurnOffNotification);
        testNotification = (Button)findViewById(R.id.testNotification);
        allowWhenDnd = (Button)findViewById(R.id.allowWhenDnd);
        chkTryAuto = (Switch)findViewById(R.id.chkTryAuto);
        chkPlaySound = (Switch)findViewById(R.id.chkPlaySound);
        chkEnableNotification = (Switch)findViewById(R.id.chkEnableNotification);
    }

    public void testcn(View view) {
        NotificatinUtils.postNoification(this);
    }

    public void testcff(View view) {
        NotificatinUtils.removeNotification(this);
    }


    public void allowwhendnd(View view) {
        NotificatinUtils.showNotificationSetting(this);
    }
}
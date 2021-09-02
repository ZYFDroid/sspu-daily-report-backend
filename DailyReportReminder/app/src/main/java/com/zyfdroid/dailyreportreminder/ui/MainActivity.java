package com.zyfdroid.dailyreportreminder.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.zyfdroid.dailyreportreminder.R;
import com.zyfdroid.dailyreportreminder.utils.AlarmChecker;
import com.zyfdroid.dailyreportreminder.utils.ConfBean;
import com.zyfdroid.dailyreportreminder.utils.SpUtils;
import com.zyfdroid.dailyreportreminder.utils.TimeUtils;


import java.util.Date;

public class MainActivity extends Activity {

    public static boolean DEBUG_MODE=false;

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        if(temperature < 350){temperature=350;}
        if(temperature > 420){temperature=420;}
        this.temperature = temperature;
        lblTemperature.setText(String.valueOf(temperature / 10f));
    }

    int temperature = 366;

    TextView lblTemperature = null;
    TextView numTemperature = null;
    Button btnStart = null;

    float seekInterval = 1;

    void initUi(){
        lblTemperature = (TextView)findViewById(R.id.lblTemperature);
        numTemperature = (TextView)findViewById(R.id.numTemperature);
        btnStart = (Button)findViewById(R.id.btnStart);
        seekInterval = dip2px(6);
        ((CheckBox)findViewById(R.id.chkDebug)).setChecked(DEBUG_MODE);
        ((CheckBox)findViewById(R.id.chkDebug)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DEBUG_MODE = isChecked;
            }
        });
        ConfBean confBean = ConfBean.getConf(this);
        if(!confBean.getAllowonekey()){
            btnStart.setText("手动\n填报");
            findViewById(R.id.manualContainer).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SpUtils.on(this).getLong(SpUtils.DAY_REPORTED,-1) >= TimeUtils.getDayStamp(new Date())){
            btnStart.setBackgroundResource(R.drawable.btn_login_material_disabled);
            btnStart.setText("已填报");
        }
        else{
            btnStart.setBackgroundResource(R.drawable.btn_login_material);
            btnStart.setText("一键\n填报");
            ConfBean confBean = ConfBean.getConf(this);
            if(!confBean.getAllowonekey()){
                btnStart.setText("手动\n填报");
                findViewById(R.id.manualContainer).setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();


        setTemperature(SpUtils.on(this).getInt(SpUtils.TEMPERATURE,366));

        numTemperature.setOnTouchListener(new View.OnTouchListener() {
            float dx;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    dx = event.getX();
                }
                if(event.getAction()==MotionEvent.ACTION_MOVE){
                    float delta = event.getX() - dx;
                    if(delta > seekInterval){
                        dx = event.getX();
                        setTemperature(getTemperature()+1);
                    }
                    if(delta < -seekInterval){
                        dx = event.getX();
                        setTemperature(getTemperature()-1);
                    }
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    SpUtils.on(MainActivity.this).edit().putInt(SpUtils.TEMPERATURE,getTemperature()).apply();
                }
                return true;
            }
        });

        setTitle("每日一报");
        if(SpUtils.on(this).getBoolean(SpUtils.FIRST_RUN,true)){
            startActivity(new Intent(this,LoginActivity.class));
        }
        AlarmChecker.checkAlarmClock(this);
    }

    public void onLoginClick(View view) {
        if(SpUtils.on(this).getBoolean(SpUtils.FIRST_RUN,true)){
            startActivity(new Intent(this,LoginActivity.class));
        }
        else{
            if(temperature < 373){
                gotoAutoReport();
            }
            else{
                new AlertDialog.Builder(this).setMessage("体温超过37.3℃，是否继续填报？").setPositiveButton("继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gotoAutoReport();
                    }
                }).setNegativeButton("取消",null).create().show();
            }
        }
    }

    public void gotoAutoReport(){
        ConfBean cb = ConfBean.getConf(this);
        if(!cb.getAllowonekey()){
            onManualReport(null);
            return;
        }
        Intent i = new Intent(this,PerformActivity.class);
        i.putExtra("temp",String.valueOf(getTemperature() / 10f));
        i.putExtra("auto",true);
        i.putExtra("un",SpUtils.on(this).getString(SpUtils.USERNAME,""));
        i.putExtra("pw",SpUtils.on(this).getString(SpUtils.PASSWORD,""));
        startActivity(i);
    }

    public void setNotification(View view) {
        startActivity(new Intent(this,NotificationActivity.class));
    }

    public float dip2px(int dp){
        return getResources().getDisplayMetrics().density * dp;
    }

    public void setAccount(View view) {
        startActivity(new Intent(this,LoginActivity.class));
    }

    public void onManualReport(View view) {
        if(SpUtils.on(this).getBoolean(SpUtils.FIRST_RUN,true)){
            startActivity(new Intent(this,LoginActivity.class));
        }
        else{
            Intent i = new Intent(this,PerformActivity.class);
            i.putExtra("temp",String.valueOf(getTemperature() / 10f));
            i.putExtra("auto",false);
            i.putExtra("un",SpUtils.on(this).getString(SpUtils.USERNAME,""));
            i.putExtra("pw",SpUtils.on(this).getString(SpUtils.PASSWORD,""));
            startActivity(i);
        }
    }

    public void onHistory(View view) {
        if(SpUtils.on(this).getBoolean(SpUtils.FIRST_RUN,true)){
            startActivity(new Intent(this,LoginActivity.class));
        }
        else{
            Intent i = new Intent(this,PerformActivity.class);
            i.putExtra("temp",String.valueOf(getTemperature() / 10f));
            i.putExtra("auto",false);
            i.putExtra("history",true);
            i.putExtra("un",SpUtils.on(this).getString(SpUtils.USERNAME,""));
            i.putExtra("pw",SpUtils.on(this).getString(SpUtils.PASSWORD,""));
            startActivity(i);
        }
    }
}
package com.zyfdroid.dailyreportreminder.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import com.zyfdroid.dailyreportreminder.R;
import com.zyfdroid.dailyreportreminder.utils.ConfBean;
import com.zyfdroid.dailyreportreminder.utils.HttpUtils;
import com.zyfdroid.dailyreportreminder.utils.SpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(SpUtils.on(SplashActivity.this).getLong(SpUtils.CONFCACHE_EXPIRE,0) < System.currentTimeMillis()){
                    try {
                        String json = HttpUtils.httpGet(HttpUtils.confUrl);
                        SpUtils.on(SplashActivity.this).edit().putString(SpUtils.CONFCACHE,json).putLong(SpUtils.CONFCACHE_EXPIRE,System.currentTimeMillis()+120000L).apply();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (HttpUtils.InvalidResponseCodeException e) {
                        e.printStackTrace();
                        if(e.responseCode<500){
                            hWnd.post(SplashActivity.this::disabled);
                            return;
                        }
                    }
                }
                try {
                    ConfBean conf = ConfBean.fill(new JSONObject(SpUtils.on(SplashActivity.this).getString(SpUtils.CONFCACHE,"")));
                    long currentVer = SpUtils.on(SplashActivity.this).getLong(SpUtils.SCRIPT_VER,0);
                    if(conf.getDisabled()){
                        hWnd.postDelayed(SplashActivity.this::disabled,1);
                        return;
                    }
                    if(currentVer < conf.getScriptversion()){
                        try {
                            String script = HttpUtils.httpGet("api/"+conf.getScript());
                            SpUtils.on(SplashActivity.this).edit().putString(SpUtils.SCRIPTCACHE,script).putLong(SpUtils.SCRIPT_VER,conf.getScriptversion()).apply();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (HttpUtils.InvalidResponseCodeException e) {
                            e.printStackTrace();
                            if(e.responseCode<500){
                                hWnd.post(SplashActivity.this::disabled);
                                return;
                            }
                        }
                    }
                    int appVer = getVersionName();
                    if(appVer < conf.getAppver()){
                        hWnd.post(SplashActivity.this::updateNeeded);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    hWnd.post(SplashActivity.this::updatefail);
                    return;
                }
                hWnd.postDelayed(SplashActivity.this::success,500);
            }
        }).start();
    }

    private void updateNeeded() {
        new AlertDialog.Builder(SplashActivity.this).setTitle("????????????").setMessage("??????APP?????????????????????").setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        }).setPositiveButton("???????????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                ConfBean cb = ConfBean.getConf(SplashActivity.this);
                Uri content_url = Uri.parse(cb.getApkurl());
                intent.setData(content_url);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).create().show();
    }

    private void success() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }


    private int getVersionName()
    {
        // ??????packagemanager?????????
        PackageManager packageManager = getPackageManager();
        // getPackageName()???????????????????????????0???????????????????????????
        PackageInfo packInfo = null;
        int version = Integer.MAX_VALUE;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
            version = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }


    private void updatefail() {
        new AlertDialog.Builder(SplashActivity.this).setTitle("????????????").setMessage("????????????????????????").setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).create().show();
    }

    private void disabled() {
        new AlertDialog.Builder(SplashActivity.this).setTitle("??????").setMessage("???????????????????????????APP?????????").setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).create().show();
    }

    Handler hWnd = new Handler(Looper.myLooper());

    private long maxEndDate = 0;
}
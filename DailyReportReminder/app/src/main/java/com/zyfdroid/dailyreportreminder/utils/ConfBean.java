package com.zyfdroid.dailyreportreminder.utils;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfBean
{
    private int code = 200;

    private boolean disabled = false;

    private String bbs = "暂无公告";

    private String script = "script.js";

    private int scriptversion = 0;

    private boolean allowauto = true;

    private boolean allowonekey = true;

    private int appver = 0;

    private String appvername = "1.0.0.0";

    private String apkurl = "https://sspu-daily.zyfdroid.com/";

    public void setCode(int code){
        this.code = code;
    }
    public int getCode(){
        return this.code;
    }
    public void setDisabled(boolean disabled){
        this.disabled = disabled;
    }
    public boolean getDisabled(){
        return this.disabled;
    }
    public void setBbs(String bbs){
        this.bbs = bbs;
    }
    public String getBbs(){
        return this.bbs;
    }
    public void setScript(String script){
        this.script = script;
    }
    public String getScript(){
        return this.script;
    }
    public void setScriptversion(int scriptversion){
        this.scriptversion = scriptversion;
    }
    public int getScriptversion(){
        return this.scriptversion;
    }
    public void setAllowauto(boolean allowauto){
        this.allowauto = allowauto;
    }
    public boolean getAllowauto(){
        return this.allowauto;
    }
    public void setAllowonekey(boolean allowonekey){
        this.allowonekey = allowonekey;
    }
    public boolean getAllowonekey(){
        return this.allowonekey;
    }
    public void setAppver(int appver){
        this.appver = appver;
    }
    public int getAppver(){
        return this.appver;
    }
    public void setAppvername(String appvername){
        this.appvername = appvername;
    }
    public String getAppvername(){
        return this.appvername;
    }
    public void setApkurl(String apkurl){
        this.apkurl = apkurl;
    }
    public String getApkurl(){
        return this.apkurl;
    }
    public static ConfBean fill(JSONObject jsonobj) throws JSONException {
        ConfBean entity = new ConfBean();
        if (jsonobj.has("code")) {
            entity.setCode(jsonobj.getInt("code"));
        }
        if (jsonobj.has("disabled")) {
            entity.setDisabled(jsonobj.getBoolean("disabled"));
        }
        if (jsonobj.has("bbs")) {
            entity.setBbs(jsonobj.getString("bbs"));
        }
        if (jsonobj.has("script")) {
            entity.setScript(jsonobj.getString("script"));
        }
        if (jsonobj.has("scriptversion")) {
            entity.setScriptversion(jsonobj.getInt("scriptversion"));
        }
        if (jsonobj.has("allowauto")) {
            entity.setAllowauto(jsonobj.getBoolean("allowauto"));
        }
        if (jsonobj.has("allowonekey")) {
            entity.setAllowonekey(jsonobj.getBoolean("allowonekey"));
        }
        if (jsonobj.has("appver")) {
            entity.setAppver(jsonobj.getInt("appver"));
        }
        if (jsonobj.has("appvername")) {
            entity.setAppvername(jsonobj.getString("appvername"));
        }
        if (jsonobj.has("apkurl")) {
            entity.setApkurl(jsonobj.getString("apkurl"));
        }
        return entity;
    }

    public static ConfBean getConf(Context ctx){
        try {
            return ConfBean.fill(new JSONObject(SpUtils.on(ctx).getString(SpUtils.CONFCACHE,"{}")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ConfBean();
    }
}

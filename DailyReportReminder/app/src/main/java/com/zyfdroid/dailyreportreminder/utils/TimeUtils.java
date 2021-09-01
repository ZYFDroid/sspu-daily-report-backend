package com.zyfdroid.dailyreportreminder.utils;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat sdf_short = new SimpleDateFormat("yyyy-MM-dd");
    public static Date epochPoint = null;
    static {
        try {
            epochPoint = sdf.parse("2000-11-20 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public static final long MILLS_PER_DAY = 86400L * 1000L;
    public static long getDayStamp(Date date){
        return (date.getTime() - epochPoint.getTime()) / MILLS_PER_DAY;
    }

    public static String[] hintPoints = {
            " 06:00:00",
            " 06:30:00",
            " 07:00:00",
            " 07:30:00",
            " 08:00:00",
            " 08:30:00",
    };

    public static String finalHintPoint = " 08:45:00";
    public static String stoptime = " 09:00:00";

    public static Date parseDayStamp(long days){
        return new Date(epochPoint.getTime() + MILLS_PER_DAY * days);
    }

    public static Date parse(String date){
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Date parseShort(String date){
        try {
            return sdf_short.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date computeNextTime(long alreadycompleted, Date date,boolean autoonly){
        date.setTime(date.getTime() + 2000L);
        long reported = alreadycompleted+1;
        long current = getDayStamp(date);
        if(date.after(parse(sdf_short.format(date)+stoptime))){current++;};
        long beginday = Math.max(current,reported);
        Date beginDate = parseDayStamp(beginday);
        Date alarmTime =new Date(beginDate.getTime());
        if(!autoonly) {
            for (int i = 0; i < hintPoints.length; i++) {
                alarmTime = parse(sdf_short.format(beginDate) + hintPoints[i]);
                if (alarmTime.after(date)) {
                    return alarmTime;
                }
            }
        }
        alarmTime = parse(sdf_short.format(beginDate)+finalHintPoint);
        if(alarmTime.after(date)){
            return alarmTime;
        }
        alarmTime = parse(sdf_short.format(beginDate)+stoptime);
        return alarmTime;
    }

    public static String computeTimeMessage(Date date){
        date.setTime(date.getTime()+2000L);
        String msg="";
        Date beginDate = parseDayStamp(getDayStamp(date));
        Date alarmTime =new Date(beginDate.getTime());
        for (int i = 0; i < hintPoints.length; i++) {
            alarmTime = parse(sdf_short.format(beginDate)+hintPoints[i]);
            if(date.after(alarmTime)){
                msg=hintMessage[i];
            }
        }
        alarmTime = parse(sdf_short.format(beginDate)+finalHintPoint);
        if(date.after(alarmTime)){
            msg = tryReportMessage;
        }
        alarmTime = parse(sdf_short.format(beginDate)+stoptime);
        if(date.after(alarmTime)) {
            msg = finalMessage;
        }
        return msg;
    }

    public static boolean computeAutoTryNecessary(Date date){
        boolean result = false;
        Date beginDate = parseDayStamp(getDayStamp(date));
        Date alarmTime =new Date(beginDate.getTime());
        alarmTime = parse(sdf_short.format(beginDate)+finalHintPoint);
        if(date.after(alarmTime)){
            result = true;
        }
        alarmTime = parse(sdf_short.format(beginDate)+stoptime);
        if(date.after(alarmTime)) {
           result = false;
        }
        return result;
    }

    public static final String[] hintMessage = {
            "早安，又是元气满满的一天|记得每日一报哦，点击打开👆", //6：00
            "起床了吗|先进行每日一报吧，点击打开👆", //6：30
            "吃个早饭吧，美好的一天从早饭开始|吃早饭的时候别忘了每日一报哦，点击打开👆", //7：00
            "美好的一天|别忘了每日一报哦，点击打开👆", //7：30
            "还没有进行每日一报呢|点击打开👆每日一报", //8：00
            "快到截至时间了|点击👆立刻每日一报"}; //8：30
    public static final String tryReportMessage = "来不及了|自动每日一报即将在1分钟后启动";//8：45
    public static final String finalMessage = "看起来你错过了每日一报|建议联系老师说明情况。点击打开👆每日一报";//9：00
}

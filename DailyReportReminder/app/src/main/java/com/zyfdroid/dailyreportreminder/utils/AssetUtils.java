package com.zyfdroid.dailyreportreminder.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AssetUtils {
    public static String getFromAssets(Context ctx, String fileName){
        try {
            InputStreamReader inputReader = new InputStreamReader( ctx.getResources().getAssets().open(fileName) ,"UTF-8");
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

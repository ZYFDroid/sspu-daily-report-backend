package com.zyfdroid.dailyreportreminder.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

public class HttpUtils {
    public static final String baseUrl = "http://sspu-daily.zyfdroid.com/";
    public static final String confUrl = "api/conf.json";
    public static String httpGet(String url) throws SocketTimeoutException, IOException, InvalidResponseCodeException {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(baseUrl+url).openConnection();
            conn.setRequestMethod("GET");
            try {
                conn.connect();
            }catch (IOException ex){
                try{
                    if(conn.getResponseCode()!=200){
                        throw new InvalidResponseCodeException(conn.getResponseCode());
                    }
                    else{
                        throw ex;
                    }
                }catch (IOException ex2){
                    throw ex2;
                }
            }
            try(InputStream in = conn.getInputStream(); InputStreamReader isr = new InputStreamReader(in); BufferedReader br = new BufferedReader(isr)){
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = br.readLine())!=null){
                    sb.append(line).append(System.lineSeparator());
                }
                return sb.toString();
            }catch (IOException ex){
                try{
                    if(conn.getResponseCode()!=200){
                        throw new InvalidResponseCodeException(conn.getResponseCode());
                    }
                    else{
                        throw ex;
                    }
                }catch (IOException ex2){
                    throw ex2;
                }
            }
        }
        catch (SocketTimeoutException ex){
            throw ex;
        }
        catch (IOException e) {
            throw e;
        }
    }

    public static class InvalidResponseCodeException extends Exception{
        public InvalidResponseCodeException(int responseCode) {
            this.responseCode = responseCode;
        }

        public int responseCode;
    }
}

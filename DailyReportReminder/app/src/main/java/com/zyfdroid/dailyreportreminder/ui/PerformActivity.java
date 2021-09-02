package com.zyfdroid.dailyreportreminder.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zyfdroid.dailyreportreminder.R;
import com.zyfdroid.dailyreportreminder.utils.AlarmChecker;
import com.zyfdroid.dailyreportreminder.utils.AssetUtils;
import com.zyfdroid.dailyreportreminder.utils.NotificatinUtils;
import com.zyfdroid.dailyreportreminder.utils.SpUtils;
import com.zyfdroid.dailyreportreminder.utils.TimeUtils;

import java.util.Date;

public class PerformActivity extends Activity {

    WebView webView;

    String infusionJavascript = null;

    LinearLayout debugUi=null;
    TextView debugText = null;
    Handler hwnd = new Handler(Looper.getMainLooper());

    Runnable autoclose = new Runnable() {
        @Override
        public void run() {
            NotificatinUtils.postNoification(PerformActivity.this,"每日一报","看起来自动填报失败了");
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView.setWebContentsDebuggingEnabled(true);
        if(getIntent().getBooleanExtra("attempt",false)){
            KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock keyguardLock = km.newKeyguardLock("1");
            keyguardLock.disableKeyguard();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            hwnd.postDelayed(autoclose,60*1000*2);

        }
        setContentView(R.layout.activity_perform);
        setTitle("填报页面");
        infusionJavascript = SpUtils.on(this).getString(SpUtils.SCRIPTCACHE,AssetUtils.getFromAssets(this,"operate.js"));
        initUi();
        initWeb();
        if(SpUtils.on(this).getBoolean(SpUtils.FIRST_RUN,true)){
            new AlertDialog.Builder(PerformActivity.this).setTitle("填报失败").setMessage("还没有设置登录信息").setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            }).setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).create().show();
            return;
        }
        webView.loadUrl(homeUrl);
    }

    public static final String homeUrl="https://hsm.sspu.edu.cn/selfreport/Default.aspx";

    @SuppressLint("SetJavaScriptEnabled")
    private void initWeb() {
        webView = new WebView(this);
        WebSettings setting = webView.getSettings();
        setting.setUseWideViewPort(true);
        setting.setAllowContentAccess(true);
        setting.setCacheMode(WebSettings.LOAD_DEFAULT);
        setting.setBuiltInZoomControls(true);
        setting.setDisplayZoomControls(false);
        setting.setLoadWithOverviewMode(true);
        setting.setLoadsImagesAutomatically(true);
        setting.setDomStorageEnabled(true);
        setting.setJavaScriptEnabled(true);
        setting.setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setting.setSafeBrowsingEnabled(false);
        }
        setting.setSupportZoom(true);
        webContainer.addView(webView,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        webView.addJavascriptInterface(new ProgressAccessor(),"ProgressAccessor");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showProgress("加载页面中");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideProgress();
                webView.evaluateJavascript(infusionJavascript,null);
                evaluteJavascriptFunction("mySetUser",getIntent().getStringExtra("un"),getIntent().getStringExtra("pw"));
                evaluteJavascriptFunction("mySetTemperature",getIntent().getStringExtra("temp"));
                if(getIntent().getBooleanExtra("auto",false)){
                    evaluteJavascriptFunction("myEnableAuto");
                }
                if(getIntent().getBooleanExtra("history",false)){
                    evaluteJavascriptFunction("mySetHistoryMode");
                }

                evaluteJavascriptFunction("myOnLoadEnd",url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Toast.makeText(PerformActivity.this, "页面加载失败", Toast.LENGTH_SHORT).show();
                hideProgress();
            }
        });

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onConsoleMessage(final ConsoleMessage consoleMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        debugText.append(parseConsoleMessage(consoleMessage));
                    }
                });
                return super.onConsoleMessage(consoleMessage);
            }
        });
    }

    String parseConsoleMessage(ConsoleMessage msg){
        StringBuilder sb = new StringBuilder();
        sb.append('<').append(msg.messageLevel().name()).append(' ').append(msg.sourceId()).append(':').append(msg.lineNumber()).append("> ").append(msg.message()).append("\r\n");
        return sb.toString();
    }

    public static final String reportUrl = "https://hsm.sspu.edu.cn/selfreport/DayReport.aspx";
    public static final String loginUrl = "https://id.sspu.edu.cn/cas/login";
    public static final String historyUrl = "https://hsm.sspu.edu.cn/selfreport/ReportHistory.aspx";

    long lasttime = -1;
    @Override
    public void onBackPressed() {
        if(getIntent().getBooleanExtra("history",false)){
            if(webView.getUrl().startsWith(historyUrl)){
                super.onBackPressed();
            }
            else{
                webView.goBack();
            }
        }
        else{
            if(System.currentTimeMillis() - lasttime > 1000){
                Toast.makeText(this, "再按一次取消填报", Toast.LENGTH_SHORT).show();
                lasttime = System.currentTimeMillis();
            }
            else{
                super.onBackPressed();
            }
        }
    }

    public void evaluteJavascriptFunction(String functionName, Object... arguments) {
        StringBuilder scriptBuilder = new StringBuilder();
        scriptBuilder.append(functionName).append("(");
        for (int i = 0; i < arguments.length; i++) {
            Object obj = arguments[i];
            if (obj == null) {
                scriptBuilder.append("null");
            } else if (obj instanceof String) {
                scriptBuilder.append(escapeText((String) obj));
            } else if (obj instanceof Boolean) {
                scriptBuilder.append(obj.toString().toLowerCase());
            } else {
                scriptBuilder.append(obj.toString());
            }
            if (i != arguments.length - 1) {
                scriptBuilder.append(",");
            }
        }
        scriptBuilder.append(");");
        webView.evaluateJavascript(scriptBuilder.toString(), null);
    }


    boolean webProgress = false;
    boolean appProgress = false;

    class ProgressAccessor{
        @JavascriptInterface
        public void showProgress(String text){
            runOnUiThread(()->{
                webProgress = true;
                txtLoading.setText(text);
                updateProgressState();
            });

        }
        @JavascriptInterface
        public void hideProgress(){
            runOnUiThread(()->{
                webProgress = false;
                updateProgressState();
            });
        }
        @JavascriptInterface
        public void success(){
            runOnUiThread(()->{
                SpUtils.on(PerformActivity.this).edit().putLong(SpUtils.DAY_REPORTED, TimeUtils.getDayStamp(new Date())).apply();
                NotificatinUtils.removeNotification(PerformActivity.this);
                AlarmChecker.checkAlarmClock(PerformActivity.this);
                hwnd.removeCallbacks(autoclose);
                new AlertDialog.Builder(PerformActivity.this).setTitle("填报成功！").setMessage("填报成功，祝你拥有美好的一天！").setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                }).setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create().show();
            });
        }
        @JavascriptInterface
        public void fail(final String reason){
            runOnUiThread(()->{
                hwnd.removeCallbacks(autoclose);
                new AlertDialog.Builder(PerformActivity.this).setTitle("填报失败！").setMessage(reason).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                }).setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create().show();
            });
        }
    }

    public void showProgress(String text){
        appProgress = true;
        txtLoading.setText(text);
        updateProgressState();
    }
    public void hideProgress(){
        appProgress = false;
        updateProgressState();
    }

    public void updateProgressState(){
        progressContainer.setVisibility((webProgress || appProgress) ? View.VISIBLE : View.INVISIBLE);
    }

    FrameLayout progressContainer = null;
    FrameLayout webContainer = null;
    TextView txtLoading = null;
    void initUi(){
        progressContainer = (FrameLayout)findViewById(R.id.progressContainer);
        webContainer = (FrameLayout)findViewById(R.id.webContainer);
        txtLoading = findViewById(R.id.txtLoading);
        debugUi= findViewById(R.id.debugView);
        debugText = findViewById(R.id.debugText);
        debugUi.setVisibility(MainActivity.DEBUG_MODE ? View.VISIBLE : View.GONE);
    }

    public static String escapeText(String src)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        for (char chr : src.toCharArray())
        {
            switch (chr) {
                case '\0':     sb.append("\\0");break;
                case '\b':     sb.append("\\b");break;
                case '\t':     sb.append("\\t");break;
                case '\n':     sb.append("\\n");break;
                case '\u000B': sb.append("\\v");break;
                case '\u000C':
                case '\r':     sb.append("\\f");break;
                case '\"':     sb.append("\\\"");break;
                case '\\':     sb.append("\\\\");break;
                default: sb.append(chr);break;
            }
        }
        return sb.append("\"").toString();
    }

    @Override
    protected void onDestroy() {
        WebView mWebView = webView;
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}
package com.zyfdroid.dailyreportreminder.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.zyfdroid.dailyreportreminder.R;
import com.zyfdroid.dailyreportreminder.utils.ConfBean;
import com.zyfdroid.dailyreportreminder.utils.HttpUtils;
import com.zyfdroid.dailyreportreminder.utils.SpUtils;


public class LoginActivity extends Activity {

    EditText txtPw = null;
    EditText txtUn = null;
    CheckBox chkEula = null;

    String eula="本应用程序出于个人兴趣编写，仅供本人使用，建议不要传播，原作者对除本人以外使用程序产生的问题概不负责，包括但不限于下列情况。\n" +
            "\n" +
            "1. 由于校园网站更新导致程序不可用的\n" +
            "2. 校方命令禁止使用本软件后，仍然使本软件，导致受到处分的\n" +
            "3. 由于操作失误产生后果的\n" +
            "4. 填写的信息与实际不符合，导致产生任何后果的\n" +
            "5. 由于设备之间的差异，导致程序不能正常工作的\n" +
            "6. 由于设备之间的差异，导致程序的提醒功能不能工作，错过每日一报的\n" +
            "7. 从不受信任的第三方下载本软件，导致账号密码泄露的\n" +
            "8. 由于网络环境波动，导致软件不可用的\n" +
            "9. 由于自然灾害导致软件不可用的\n" +
            "\n" +
            "勾选 同意免责声明 即表示您已阅读并充分理解上述内容，并愿意承担软件产生的问题，若不同意，请卸载软件。";

    void initUi(){
        txtPw = (EditText)findViewById(R.id.txtPw);
        txtUn = (EditText)findViewById(R.id.txtUn);
        chkEula = findViewById(R.id.chkAcceptEula);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_login);
        setTitle("设置账号");
        initUi();
        txtUn.setText(SpUtils.on(this).getString(SpUtils.USERNAME,""));
        txtPw.setText(SpUtils.on(this).getString(SpUtils.PASSWORD,""));
        chkEula.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    new AlertDialog.Builder(LoginActivity.this).setTitle("免责声明").setMessage(eula).setPositiveButton(android.R.string.ok,null).create().show();
                }
            }
        });
    }



    public void onLoginClick(View view) {
        if(!chkEula.isChecked()){
            Toast.makeText(this, "请阅读免责声明", Toast.LENGTH_SHORT).show();
            return;
        }
        if(txtUn.getText().length()>6 && txtPw.getText().length() >=6){
            SpUtils.on(this).edit().putString(SpUtils.USERNAME,txtUn.getText().toString()).putString(SpUtils.PASSWORD,txtPw.getText().toString()).putBoolean(SpUtils.FIRST_RUN,false).apply();
            finish();
        }
        else{
            Toast.makeText(this, "账号密码无效", Toast.LENGTH_SHORT).show();
        }
    }

    public void showBBS(View view) {
        ConfBean conf = ConfBean.getConf(this);
        new AlertDialog.Builder(this).setTitle("公告").setMessage(conf.getBbs()).setPositiveButton("确定", null).create().show();
    }
}
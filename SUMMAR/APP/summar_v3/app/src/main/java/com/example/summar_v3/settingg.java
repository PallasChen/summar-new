package com.example.summar_v3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class settingg extends AppCompatActivity{
    //登出按鈕
    TextView logout;

    //開關
    Switch OnOrOffbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingg);
        logout=findViewById(R.id.exittext);

        final SharedPreferences flag =getSharedPreferences("ischeck",MODE_PRIVATE);
        final SharedPreferences.Editor editor = flag.edit();

        //開啟floating_button
        OnOrOffbtn =  findViewById(R.id.switch2);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

//        判斷是否開啟
        if(flag.getInt("flag",0)==1){
            OnOrOffbtn.setChecked(true);
        }
        else {OnOrOffbtn.setChecked(false);
        }

        //switch開啟關閉floatingButton
        OnOrOffbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //添加懸浮button
                    if(Build.VERSION.SDK_INT>=23){//使用系统级别的WindowManager展示悬浮框，需要6.0以上的权限；
                        if(!Settings.canDrawOverlays(getApplicationContext())){
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(Uri.parse( "package:"+getPackageName()));  //应用的包名，可直接跳转到这个应用的悬浮窗设置；
                            editor.putInt("flag",0).commit();
                            startActivity(intent);

                        }else{
                            Toast.makeText(settingg.this,"開啟懸浮按鈕",Toast.LENGTH_SHORT).show();
                            editor.putInt("flag",1).commit();
                            //啟動懸浮視窗
                            startService(new Intent(getApplicationContext(),FloatingWindow.class));
                            finish();
                        }
                    }
                }else{
                    //移除懸浮button
                    editor.putInt("flag",0).commit();
                    //終止懸浮視窗
                    stopService(new Intent(getApplicationContext(),FloatingWindow.class));
                    Toast.makeText(settingg.this,"關閉懸浮按鈕",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //登出
    public void logout(View v){
        SharedPrefManger.getInstance(this).logout();
        finish();
        startActivity(new Intent(this,login.class));
    }

}

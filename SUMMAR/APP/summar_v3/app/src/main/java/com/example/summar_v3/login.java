package com.example.summar_v3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class login extends AppCompatActivity {
    Button login,login_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(SharedPrefManger.getInstance(this).isLogin()){ //已登錄
            finish();
            Intent intent = new Intent(this,home.class);
            intent.putExtra("isFill","1");  //啟動app 才會顯示問卷
            startActivity(intent);
            return;
        }

        login=findViewById(R.id.login);
        login_=findViewById(R.id.login_);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(login.this,MainActivity.class);
                startActivity(intent);
            }
        });
        login_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(login.this,home.class);
                startActivity(intent);
            }
        });
    }
}

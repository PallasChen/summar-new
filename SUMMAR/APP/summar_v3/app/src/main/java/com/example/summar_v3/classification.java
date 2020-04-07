package com.example.summar_v3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

public class classification extends AppCompatActivity implements View.OnClickListener {
    Button button1,button2,button3,button4,button5,button6,button7;
    Button b1,b2,b3,b4,b5;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classification);
        Toolbar toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle("分類");
        setSupportActionBar(toolbar);
        //分類
        button1=findViewById(R.id.button1);
        button1.setOnClickListener(this);
        button2=findViewById(R.id.button2);
        button2.setOnClickListener(this);
        button3=findViewById(R.id.button3);
        button3.setOnClickListener(this);
        button4=findViewById(R.id.button4);
        button4.setOnClickListener(this);
        button5=findViewById(R.id.n);
        button5.setOnClickListener(this);
        button6=findViewById(R.id.button6);
        button6.setOnClickListener(this);
        button7=findViewById(R.id.edu);
        button7.setOnClickListener(this);
        //工具列
        b1=findViewById(R.id.bt1);
        b1.setOnClickListener(this);
        b2=findViewById(R.id.bt2);
//        b2.setOnClickListener(this);
        b2.setBackgroundResource(R.drawable.claclick);
        b3=findViewById(R.id.bt3);
        b3.setOnClickListener(this);
        b4=findViewById(R.id.bt4);
        b4.setOnClickListener(this);
        b5=findViewById(R.id.bt5);
        b5.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.button1:
                // code for button when user clicks buttonOne.
                intent.setClass(getApplicationContext(),Article.class);
                intent.putExtra("c",button1.getText());
                getCount((String) button1.getText());
                startActivity(intent);
                break;
            case R.id.button2:
                // do your code
                intent.setClass(getApplicationContext(),Article.class);
                intent.putExtra("c",button2.getText());
                getCount((String) button2.getText());
                startActivity(intent);
                break;
            case R.id.button3:
                // do your code
                intent.setClass(getApplicationContext(),Article.class);
                intent.putExtra("c",button3.getText());
                getCount((String) button3.getText());
                startActivity(intent);
                break;
            case R.id.button4:
                // do your code
                intent.setClass(getApplicationContext(),Article.class);
                intent.putExtra("c",button4.getText());
                getCount((String) button4.getText());
                startActivity(intent);
                break;
            case R.id.n:
                // do your code
                intent.setClass(getApplicationContext(),Article.class);
                intent.putExtra("c",button5.getText());
                getCount((String) button5.getText());
                startActivity(intent);
                break;
            case R.id.button6:
                // do your code
                intent.setClass(getApplicationContext(),Article.class);
                intent.putExtra("c",button6.getText());
                getCount((String) button6.getText());
                startActivity(intent);
                break;
            case R.id.edu: //教育
                // do your code
                intent.setClass(getApplicationContext(),Article.class);
                intent.putExtra("c",button7.getText());
                getCount((String) button7.getText());
                startActivity(intent);
                break;
            case R.id.bt1:
                // code for button when user clicks buttonOne.
                intent.setClass(this, home.class);
                startActivity(intent);
                break;
            case R.id.bt3:
                // do your code
                //Toast.makeText(this,"搜尋",Toast.LENGTH_SHORT).show();
                intent.setClass(this,Search.class);
                startActivity(intent);
//                startActivity(intent);
                break;
            case R.id.bt4:
                // do your code
                intent.setClass(this,following.class);
                startActivity(intent);
                break;
            case R.id.bt5:
                // do your code
                intent.setClass(this,personal.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }
    //**************
    public void getCount(String classification){
        try {
            JSONArray jsonArray;
            String useremail=SharedPrefManger.getInstance(this).getUserEmail();
            String c="";
            if (classification.equals("全球")){
                c="Global";
            }else if (classification.equals("產經")){
                c="Business";
            }else if(classification.equals("政治")){
                c="Politics";
            }else if(classification.equals("數位")){
                c="Digital";
            }else if(classification.equals("生活")){
                c="Life";
            }else if(classification.equals("運動")){
                c="Sport";
            }else if(classification.equals("教育")){
                c="Edu";
            }

            String getNum=DBConnector.executeQuery("sql=Select "+c+" From member WHERE email='"+useremail+"'");
            jsonArray=new JSONArray(getNum);
            JSONObject jsonData = jsonArray.getJSONObject(0);
            String temp=jsonData.getString(c);
            int Num=Integer.parseInt(temp);
            Num++;
            String saveNum=DBConnector.executeQuery("sql=UPDATE member SET "+c+"='"+Num+"'WHERE email='"+useremail+"'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //**************

}

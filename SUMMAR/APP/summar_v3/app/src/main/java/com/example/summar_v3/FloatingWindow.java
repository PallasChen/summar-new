package com.example.summar_v3;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;


public class FloatingWindow extends Service implements View.OnClickListener{
    public int windowcount = 0;
    //WWindowManager 變數（懸浮按鈕）,原文
    private WindowManager fab_windowManager,org_windowManager,sum_windowManager;
    private WindowManager.LayoutParams fab_params,org_params,sum_params;
    ConstraintLayout org_layout,sum_layout;
    ScrollView org_sc,sum_sc;
    //移動懸浮按鈕
    ImageButton fab;

    //原文、摘要標題
    TextView orgarticle,summary;
    //原文、摘要放置處
    EditText textarea1,textarea2;
    //原文->摘要Button
    ImageButton next_step,back_step;

    //製成摘要
    Test summar;
    //伺服器ip、port
    String ipAddr = "163.13.201.70" ;
//    String ipAddr = "172.20.10.3";
    //    int port = 5555 ;
    //伺服器
    private Thread thread;              //執行緒
    private Socket querySocket;        //客戶端的socket

    DataOutputStream dos;
    DataInputStream dis;
    String res;
    String server_sum;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createFAB();
        createORG();
        createSUM();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(org_layout.isShown()){
            org_windowManager.removeView(org_layout);
        }
        if(sum_layout.isShown()){
            sum_windowManager.removeView(sum_layout);
        }
        if (fab != null) {
            fab_windowManager.removeView(fab);
        }

    }
    // 製成懸浮視窗按鈕
    public void createFAB(){
        fab_windowManager= (WindowManager) getSystemService(WINDOW_SERVICE);

        fab_params= new WindowManager.LayoutParams(
                250,
                250,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
                );
        fab_params.x=0;
        fab_params.y=0;
        fab_params.gravity= Gravity.CENTER| Gravity.CENTER;

        fab=new ImageButton(this);
        fab.setImageResource(R.drawable.logo_fab); //設定圖片
        fab.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {//設定背景為透明
            fab.setBackground(Drawable.createFromPath("#FFFFFF"));
        }
        fab.setClickable(true);
        fab.setId(R.id.fab_id);
        fab.setOnClickListener(this);
        fab.setLayoutParams(fab_params);

        fab_windowManager.addView(fab,fab_params);

        //移動
        fab.setOnTouchListener(new View.OnTouchListener() {
            private WindowManager.LayoutParams updateParameters = fab_params;
            int x,y;
            float touchedX,touchedY;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x=updateParameters.x;
                        y=updateParameters.y;

                        touchedX=motionEvent.getRawX();
                        touchedY=motionEvent.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateParameters.x= (int) (x+(motionEvent.getRawX()-touchedX));
                        updateParameters.y= (int) (y+(motionEvent.getRawY()-touchedY));

                        fab_windowManager.updateViewLayout(fab,updateParameters);
                    default:
                        break;
                }
                return false;
            }
        });
    }
    // 製成懸浮視窗原文
    public void createORG(){
        org_windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        org_params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        org_params.gravity = Gravity.TOP | Gravity.CENTER;
//        org_p.horizontalMargin = 300;
//        org_p.verticalMargin = 300;
        org_params.x = 0;
        org_params.y = 250;
        org_params.width = 900;
        org_params.height = 1300;

        //外層ConstraintLayout製作
        org_layout = new ConstraintLayout(this);
        org_layout.setBackgroundColor(Color.WHITE);

        //ScrollView製作
        org_sc = new ScrollView(this);
        org_layout.addView(org_sc,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //原文TextView
        orgarticle = new TextView(this);
        orgarticle.setText("原文");
        orgarticle.setTextSize(28);
        org_layout.addView(orgarticle,300,150);

        //填入原文的Textarea
        textarea1 = new EditText(org_sc.getContext());
        //提示字
        textarea1.setHint("請填入想做成摘要的文章");
        //輸入的型態
        textarea1.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        textarea1.setGravity(Gravity.TOP| Gravity.LEFT);
        textarea1.setTextIsSelectable(true);
        textarea1.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        textarea1.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        textarea1.setHorizontallyScrolling(false);
        textarea1.setLines(13);
        textarea1.setSelectAllOnFocus(true);
//        textarea1.clearFocus();
        textarea1.setX(0);
        textarea1.setY(80);
        org_layout.addView(textarea1,800,1000);

        //下一步按鈕，製成摘要
        next_step = new ImageButton(org_sc.getContext());
        next_step.setImageResource(R.drawable.next);
        next_step.setId(R.id.next_stepbtn);
        next_step.setBackgroundColor(Color.WHITE);
        next_step.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        next_step.setAdjustViewBounds(true);
        next_step.setOnClickListener(this);
        next_step.setClickable(true);
        next_step.setX(700);
        next_step.setY(1100);
        org_layout.addView(next_step,180,180);
    }
    public void createSUM(){
        sum_windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        //第二個懸浮視窗 製作
        sum_params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        sum_params.gravity = Gravity.TOP | Gravity.CENTER;
        sum_params.x = 0;
        sum_params.y = 250;
        sum_params.width = 900;
        sum_params.height = 1300;

        //外層ConstraintLayout製作
        sum_layout = new ConstraintLayout(this);
        sum_layout.setBackgroundColor(Color.WHITE);

        //ScrollView製作
        sum_sc = new ScrollView(this);
        sum_layout.addView(sum_sc,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //摘要TextView
        summary = new TextView(this);
        summary.setText("摘要");
        summary.setTextSize(28);
        sum_layout.addView(summary,300,150);

        //出現摘要的Textarea
        textarea2 = new EditText(sum_sc.getContext());
        textarea2.setHint("程式生成的摘要");
        textarea2.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        textarea2.setGravity(Gravity.TOP| Gravity.LEFT);
        textarea2.setTextIsSelectable(true);
        textarea2.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        textarea2.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        textarea2.setHorizontallyScrolling(false);
        textarea2.setLines(13);
        textarea2.setX(0);
        textarea2.setY(80);

        sum_layout.addView(textarea2,800,1000);

        //返回上一部按鈕，回原文
        back_step = new ImageButton(sum_sc.getContext());
        back_step.setImageResource(R.drawable.back);
        back_step.setId(R.id.back_stepbtn);
        back_step.setBackgroundColor(Color.WHITE);
        back_step.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        back_step.setAdjustViewBounds(true);
        back_step.setOnClickListener(this);
        back_step.setClickable(true);
        back_step.setX(700);
        back_step.setY(1100);
        sum_layout.addView(back_step,180,180);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==fab.getId()){
            switch(windowcount){
                case 0:
                    //添加原文懸浮view
                    org_windowManager.addView(org_layout,org_params);
                    Toast.makeText(getApplicationContext(),"開啟懸浮視窗", Toast.LENGTH_SHORT).show();
                    windowcount = 1;
                    break;
                case 1:
                    //移除原文和摘要懸浮view
                    textarea1.setText("");
                    textarea2.setText("");
                    if(org_layout.isShown()){
                        org_windowManager.removeView(org_layout);
                    }
                    Toast.makeText(getApplicationContext(),"關閉懸浮視窗", Toast.LENGTH_SHORT).show();
                    windowcount = 0;
                    break;
            }
        }
        else if(v.getId() == next_step.getId()){
            org_windowManager.removeView(org_layout);
            
            //啟動摘要執行緒
            thread = new Thread(Connection);
            thread.start();

            //添加摘要懸浮view
            sum_windowManager.addView(sum_layout, sum_params);
            Toast.makeText(getApplicationContext(),"製成摘要", Toast.LENGTH_SHORT).show();
        }else if(v.getId() == back_step.getId()){
            //添加原文懸浮view
            org_windowManager.addView(org_layout,org_params);
            //移除摘要懸浮view
            sum_windowManager.removeView(sum_layout);
            Toast.makeText(getApplicationContext(),"原文", Toast.LENGTH_SHORT).show();
        }
    }

    //連結socket伺服器做傳送與接收
    private Runnable Connection=new Runnable(){
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try{
                // IP為Server端
                InetAddress serverIp = InetAddress.getByName(ipAddr);
                int serverPort = 5050;

                querySocket = new Socket(serverIp, serverPort);


                // 當連線後
                while (querySocket.isConnected()) {
                    // === (1) 傳送上傳命令給Server
                    dos = new DataOutputStream(querySocket.getOutputStream()) ;
//                    bw = new BufferedWriter(new OutputStreamWriter(querySocket.getOutputStream()));


                    dos.writeUTF("GetORG");
                    //原文
                    dos.writeUTF(textarea1.getText().toString());
                    dos.flush();

                    // === (2) 等待接收Server的回應
                    dis = new DataInputStream(querySocket.getInputStream()) ;
//                    br = new BufferedReader(new InputStreamReader(querySocket.getInputStream()));
                    res = dis.readUTF(); // 讀取回應字串
                    System.out.println("response="+res) ;
                    if (res.equalsIgnoreCase("Success")) {
                        // ==== 從server端傳回的摘要 ====

                        server_sum = dis.readUTF();
                        textarea2.setText(server_sum);

                    }
                }

            }catch(Exception e){
                //當斷線時會跳到catch,可以在這裡寫上斷開連線後的處理
                e.printStackTrace();
                Log.e("text","Socket連線="+e.toString());
                //finish();    //當斷線時自動關閉房間
            }

            try {
                dos.close();
                dis.close();
                querySocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };
}

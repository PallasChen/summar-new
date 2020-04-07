package com.example.summar_v3;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity implements View.OnClickListener,Serializable{
    ConnectivityManager manager;
    Button b1,b2,b3,b4,b5; //工具列按鈕
    AlertDialog dialog=null;
    String Q1="";
    String Q2="";
    private static ViewPager viewPager;
    static Myadapter myadapter;
    private ImageView [] dots; // ViewPager上的点点
    private int dotscount=5;
    private LinearLayout sliderDotspanel;
    JSONArray jsonArray,jsonArray1;
    static ListView listView;
    static List<News> newsList_news=new ArrayList<>(); //最新
    static List<News> newsList_hot=new ArrayList<>(); //熱門
    TextView tv,t;
    int isFillIn=1; //是否填寫  0->false,1->true
    String useremail; //使用者信箱
    String username; //使用者名稱
    private static Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    int currentItem = viewPager.getCurrentItem();
                    currentItem++;
                    viewPager.setCurrentItem(currentItem);
                    mHandler.sendEmptyMessageDelayed(1, 4000);
                    break;
                default:
                    break;
            }
        }

    };
    public Handler getmHandler;
    public View ftView;
    public boolean isLoading =false;
    int currentCount = 50; //第一次讀取的筆數
    int nextCount=20;//更新的筆數

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_stub);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("SUMMAR");
        setSupportActionBar(toolbar);
        //ViewStub
        ((ViewStub )findViewById(R.id.stub)).setVisibility(View.VISIBLE);
        //定義工具列跟監聽
        b1=findViewById(R.id.bt1);
//        b1.setOnClickListener(this);
        b1.setBackgroundResource(R.drawable.homeclick);
        b2=findViewById(R.id.bt2);
        b2.setOnClickListener(this);
        b3=findViewById(R.id.bt3);
        b3.setOnClickListener(this);
        b4=findViewById(R.id.bt4);
        b4.setOnClickListener(this);
        b5=findViewById(R.id.bt5);
        b5.setOnClickListener(this);
        tv=findViewById(R.id.textView);
        //檢測網路是否連線
        checkNetworkState();
        //定義listview
        listView=findViewById(R.id.listview);
        //底部view(載入更多...)
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView=li.inflate(R.layout.footer_view,null);
        //
        getmHandler = new MyHandler();
        //使用者帳號（信箱）
        useremail=SharedPrefManger.getInstance(this).getUserEmail();
        //使用者名稱
        username=SharedPrefManger.getInstance(this).getUserName();
        //資料庫連線設定
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

        getData_Fill();//資料庫取資料_填寫問券
//        getData_news();//資料庫取資料 取30筆
        getJSON("http://172.20.10.2/conn.php"); //本機
        getJSON_list("http://172.20.10.2/getlist.php"); //本機
//        getData_hot(); //只取5筆



        //從MainActivity啟動從會顯示問卷
        String isFill_status = getIntent().getStringExtra("isFill");
        if(isFill_status!=null){
            //判斷是否已填問卷
            if(isFillIn==0){
                //載入的介面，當被載入Activity後才可以用findViewById來獲得其中界面的元素

                LayoutInflater inflater = LayoutInflater.from(home.this);
                final View v = inflater.inflate(R.layout.dialog, null);

                dialog = new AlertDialog.Builder(this).create();
                dialog.setView(v);
                dialog.setCancelable(false);//按对话框以外的地方不起作用。按返回键也不起作用
                dialog.show();

                dialog.getWindow().setBackgroundDrawableResource(R.drawable.show_view); //設定圓角

                //顯示使用者帳號
                TextView name = v.findViewById(R.id.name);
                name.setText("HI! "+username);

                ImageButton close = v.findViewById(R.id.imageButton); //close
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button button = v.findViewById(R.id.button); //送出
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckBox global = v.findViewById(R.id.checkBox);
                        CheckBox poitics = v.findViewById(R.id.checkBox3);
                        CheckBox digital = v.findViewById(R.id.checkBox8);
                        CheckBox business = v.findViewById(R.id.checkBox2);
                        CheckBox sport = v.findViewById(R.id.checkBox7);
                        CheckBox life = v.findViewById(R.id.checkBox9);
                        EditText q2_ed = v.findViewById(R.id.textView3);
                        TextView q1 = v.findViewById(R.id.textView);
                        TextView q2 = v.findViewById(R.id.textView2);

                        if(global.isChecked()){ //全球
                            Q1+=global.getText()+",";
                        }
                        if(poitics.isChecked()){ //政治
                            Q1+=poitics.getText()+",";
                        }
                        if(digital.isChecked()){ //數位
                            Q1+=digital.getText()+",";
                        }
                        if(sport.isChecked()){ //運動
                            Q1+=sport.getText()+",";
                        }
                        if(life.isChecked()){ //生活
                            Q1+=life.getText()+",";
                        }
                        if(business.isChecked()){ //產經
                            Q1+=business.getText()+",";
                        }
                        Q2=q2_ed.getText().toString();

                        if(Q1.isEmpty() || Q1.equals("")){q1.setError("至少勾選一項");
                            Toast.makeText(getApplicationContext(),"至少勾選一項",Toast.LENGTH_SHORT).show();} //至少要勾選一項
                        else if(Q2.isEmpty() || Q2.equals("")){q2.setError("不能為空");
                            Toast.makeText(getApplicationContext(),"不能為空",Toast.LENGTH_SHORT).show();} //判斷Q2不能為空
                        else{
                            //填入資料庫
                            putData(Q1,Q2);
                            Toast.makeText(getApplicationContext(),Q1+"\n"+Q2,Toast.LENGTH_LONG).show();
                            dialog.dismiss(); //關閉dialog
                        }
                    }
                });
            }
            else{ //已填寫
                //Toast.makeText(getApplicationContext(),isFillIn+"00",Toast.LENGTH_LONG).show();
            }
        }
        else{}

        myadapter = new Myadapter(this);
//        myadapter=new Myadapter(this,newsList_news);
        myadapter.addListToadapter(newsList_news);
        listView.setAdapter(myadapter);
        listView.setItemsCanFocus(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //-----當觸及項目是做啥動作？可以傳值過去Article跟做使用者分析
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //item的值
                News news = (News) myadapter.getItem(position);
//                Toast.makeText(home.this,news.getTitle(),Toast.LENGTH_SHORT).show();
                //**************
                getCount(news.getClassification());
                UserAnalyze ua=new UserAnalyze(useremail);
                Toast.makeText(home.this,ua.getPercent()+'\n'+ua.getName(),Toast.LENGTH_SHORT).show();
                //**************
                Intent intent = new Intent(getApplicationContext(),Article.class);
                intent.putExtra("id", news.getId());
                intent.putExtra("title", news.getTitle());
                intent.putExtra("sa", news.getSummary());
                intent.putExtra("classification", news.getClassification());
                intent.putExtra("date", news.getDate());
                intent.putExtra("url", news.getUrl());
                intent.putExtra("pic", news.getImageUrl());
                intent.putExtra("keyword",news.getKeyword());
                intent.putExtra("citizen_id",news.getCitizen_id());
                startActivity(intent);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(view.getLastVisiblePosition() == totalItemCount-1 && listView.getCount()>30 && isLoading==false){
                    isLoading=true;
                    currentCount+=20;
                    Thread thread = new ThreadGetMoreData();
                    thread.start();
                }
            }
        });


        //定義播放照片
        viewPager = (ViewPager)findViewById(R.id.viewpager);

        //定義小圓點...
        sliderDotspanel = (LinearLayout)findViewById(R.id.viewGroup);

        //定義title顯現的地方
        tv=findViewById(R.id.textwhite);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this,newsList_hot);

        viewPager.setAdapter(viewPagerAdapter);

        dots = new ImageView[dotscount];

        for(int i = 0;i<dotscount;i++){
            ImageView iv = new ImageView(home.this);
            if (i == 0) {
                iv.setImageResource(R.drawable.active_dot); //當前（被選到的）的底線
            } else {
                iv.setImageResource(R.drawable.nonactive_dot);
            }


            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8,0,8,0);


            sliderDotspanel.addView(dots[i],params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.active_dot)); //預設第一張




        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) { // 當頁面被選取時執行
                for(int i = 0;i<dotscount;i++){
                    if(i==position%5) {
                        dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                    }
                    else {
                        dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
        mHandler.obtainMessage(1).sendToTarget();
    }

    public class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    //add loading view during
                    listView.addFooterView(ftView);
                    break;
                case 1:
                    //Update data and UI
                    myadapter.addListToadapter((ArrayList<News>)msg.obj);
                    //remove footer View after updata listview
                    listView.removeFooterView(ftView);
                    isLoading=false;
                    break;
                default:
                    break;
            }
        }
    }
    private ArrayList<News> getMoreData(){
        ArrayList<News>list = new ArrayList<>();
        try {
        String result=DBConnector.executeQuery("sql=Select * From summar.new ORDER BY date DESC Limit "+currentCount+","+nextCount);
            jsonArray=new JSONArray(result);
            News news = null;
            for(int i=0;i<20;i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                String id = jsonData.getString("id");
                String pic = jsonData.getString("pic");
                String title=jsonData.getString("title");
                String sa=jsonData.getString("sa");
                String url = jsonData.getString("url");
                String classification = jsonData.getString("classification");
                String date = jsonData.getString("date");
                String keyword = jsonData.getString("keyword");
                String citizen_id = jsonData.getString("citizen_id");

                //將資料存進News，跟List
                news=new News(id,pic,title,sa,url,date,classification,keyword,citizen_id);
                list.add(news);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        News news=null;
        return list;
    }
    public class ThreadGetMoreData extends Thread{
        @Override
        public void run() {
            //add footer view after get data
            getmHandler.sendEmptyMessage(0);
            //Search more data
            ArrayList<News> listResult=getMoreData();
            //Delay time to show loading footer view when debug,remove it when release
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //send the result to Hender
            Message msg = getmHandler.obtainMessage(1,listResult);
            getmHandler.sendMessage(msg);
        }
    }

    public void getCount(String classification){
        try {
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
            }
            String getNum=DBConnector.executeQuery("sql=Select "+c+" From member WHERE email='"+useremail+"'");
            jsonArray=new JSONArray(getNum);
            JSONObject jsonData = jsonArray.getJSONObject(0);
            String temp=jsonData.getString(c);
            int Num=Integer.parseInt(temp);
            Num++;
            String saveNum=DBConnector.executeQuery("sql=UPDATE member SET "+c+"='"+Num+"'WHERE email='"+useremail+"'");
            jsonArray=new JSONArray(saveNum);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //**************

    private void getData_Fill() {
        try {
            String result=DBConnector.executeQuery("sql=Select * From summar.member where email='"+useremail+"'");
            //String result=DBConnector.executeQuery("sql=Select * From summar.member");
            jsonArray=new JSONArray(result);
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonData = jsonArray.getJSONObject(0);
                isFillIn = jsonData.getInt("isFill");
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //取文章資料
    private void getJSON(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Integer, String> {


            @Override
            protected void onPreExecute() { //之前
                super.onPreExecute();
            }

            @Override
            protected void onProgressUpdate(Integer... values) { //等待畫面 過程
                super.onProgressUpdate();
            }

            @Override
            protected void onPostExecute(String s) { //之後
                super.onPostExecute(s);
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                try {
                jsonArray=new JSONArray(s);
                News news=null;
                for(int i=0;i<50;i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    String id = jsonData.getString("id");
                    String pic = jsonData.getString("pic_url");
                    String title=jsonData.getString("title");
                    String sa=jsonData.getString("sa");
                    String url = jsonData.getString("url");
                    String classification = jsonData.getString("classification");
                    String date = jsonData.getString("date");
                    String keyword = jsonData.getString("keyword");
                    String citizen_id = jsonData.getString("citizen_id");

                    //將資料存進News，跟List
                    news=new News(id,pic,title,sa,url,date,classification,keyword,citizen_id);
                    newsList_hot.add(news);
                }
                }catch (Exception e){
                }
            }

            @Override
            protected String doInBackground(Void... voids) { //連線...
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    Thread.sleep(2000);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n"); }
                    return sb.toString();
                } catch (Exception e) {
                    return e.getMessage();
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }
    private void getJSON_list(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Integer, String> {


            @Override
            protected void onPreExecute() { //之前
                super.onPreExecute();
            }

            @Override
            protected void onProgressUpdate(Integer... values) { //等待畫面 過程
                super.onProgressUpdate();
            }

            @Override
            protected void onPostExecute(String s) { //之後
                super.onPostExecute(s);
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                try {
                    jsonArray=new JSONArray(s);
                    News news=null;
                    for(int i=0;i<50;i++) {
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        String id = jsonData.getString("id");
                        String pic = jsonData.getString("pic_url");
                        String title=jsonData.getString("title");
                        String sa=jsonData.getString("sa");
                        String url = jsonData.getString("url");
                        String classification = jsonData.getString("classification");
                        String date = jsonData.getString("date");
                        String keyword = jsonData.getString("keyword");
                        String citizen_id = jsonData.getString("citizen_id");

                        //將資料存進News，跟List
                        news=new News(id,pic,title,sa,url,date,classification,keyword,citizen_id);
                        newsList_news.add(news);
                    }
                }catch (Exception e){
                }
            }

            @Override
            protected String doInBackground(Void... voids) { //連線...
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    Thread.sleep(2000);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n"); }
                    return sb.toString();
                } catch (Exception e) {
                    return e.getMessage();
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }
    public void getData_news(){
    try {
        String result=DBConnector.executeQuery("sql=Select * From original ORDER BY date DESC Limit 50");
        newsList_news.clear();
        jsonArray=new JSONArray(result);
        News news=null;
        for(int i=0;i<50;i++) {
            JSONObject jsonData = jsonArray.getJSONObject(i);
            String id = jsonData.getString("id");
            String pic = jsonData.getString("pic_url");
            String title=jsonData.getString("title");
            String sa=jsonData.getString("sa");
            String url = jsonData.getString("url");
            String classification = jsonData.getString("classification");
            String date = jsonData.getString("date");
            String keyword = jsonData.getString("keyword");
            String citizen_id = jsonData.getString("citizen_id");

            //將資料存進News，跟List
            news=new News(id,pic,title,sa,url,date,classification,keyword,citizen_id);
            newsList_news.add(news);
        }
    }catch (Exception e){
    }
}
    public void getData_hot(){
        try {
            newsList_hot.clear();
            String result1=DBConnector.executeQuery("sql=Select * From originl ORDER BY date DESC Limit 3");
            String result=DBConnector.executeQuery("sql=Select * From original where citizen_id != '' ORDER BY date DESC Limit 2");
            jsonArray=new JSONArray(result);
            jsonArray1=new JSONArray(result1);
            News news=null;
            News news1=null;
            //顯現5篇
            for(int i=0;i<2;i++) {
                JSONObject jsonData1 = jsonArray1.getJSONObject(i);
                String id1 = jsonData1.getString("id");
                String pic1 = jsonData1.getString("pic");
                String title1=jsonData1.getString("title");
                String sa1=jsonData1.getString("sa");
                String url1 = jsonData1.getString("url");
                String classification1 = jsonData1.getString("classification");
                String date1 = jsonData1.getString("date");
                String keyword1= jsonData1.getString("keyword");
                String citizen_id1 = jsonData1.getString("citizen_id");
                news1=new News(id1,pic1,title1,sa1,url1,date1,classification1,keyword1,citizen_id1);
                //==========公民有話要說
                JSONObject jsonData = jsonArray.getJSONObject(i);
                String id = jsonData.getString("id");
                String pic = jsonData.getString("pic");
                String title=jsonData.getString("title");
                String sa=jsonData.getString("sa");
                String url = jsonData.getString("url");
                String classification = jsonData.getString("classification");
                String date = jsonData.getString("date");
                String keyword = jsonData.getString("keyword");
                String citizen_id = jsonData.getString("citizen_id");

                //將資料存進News，跟List
                news=new News(id,pic,title,sa,url,date,classification,keyword,citizen_id);
                newsList_hot.add(news1);
                newsList_hot.add(news);
            }
            JSONObject jsonData1 = jsonArray1.getJSONObject(2);
            String id1 = jsonData1.getString("id");
            String pic1 = jsonData1.getString("pic");
            String title1=jsonData1.getString("title");
            String sa1=jsonData1.getString("sa");
            String url1 = jsonData1.getString("url");
            String classification1 = jsonData1.getString("classification");
            String date1 = jsonData1.getString("date");
            String keyword1= jsonData1.getString("keyword");
            String citizen_id1 = jsonData1.getString("citizen_id");
            news1=new News(id1,pic1,title1,sa1,url1,date1,classification1,keyword1,citizen_id1);
            newsList_hot.add(news1);
        }catch (Exception e){
        }
    }
    public void putData(String Q1,String Q2){
        try {
            String result=DBConnector.executeQuery("sql=UPDATE member SET isFill=1,Q1='"+Q1+"', Q2='"+Q2+"' WHERE email='"+useremail+"'");
            jsonArray=new JSONArray(result);
        }catch (Exception e){
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.bt2:
                // do your code
                intent.setClass(this,classification.class);
                startActivity(intent);
                break;
            case R.id.bt3:
                // do your code
//                startActivity(intent);
                intent.setClass(this,Search.class);
                startActivity(intent);
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

    //檢測網路是否連線
    private boolean checkNetworkState(){
        boolean flag = false;
        //得到網路連線資訊
                manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //去進行判斷網路是否連線
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        if (!flag) {
            setNetwork();
        }
        return flag;
    }
    private void setNetwork(){
    Toast.makeText(this, "wifi is closed!", Toast.LENGTH_SHORT).show();
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("網路提示資訊");
        builder.setMessage("網路不可用，如果繼續，請先設定網路！");
        builder.setPositiveButton("設定", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent intent = null;
/**
 * 判斷手機系統的版本！如果API大於10 就是3.0
 * 因為3.0以上的版本的設定和3.0以下的設定不一樣，呼叫的方法不同
 */
            if (android.os.Build.VERSION.SDK_INT > 10) {
                intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
            } else {
                intent = new Intent();
                ComponentName component = new ComponentName(
                        "com.android.settings",
                        "com.android.settings.WirelessSettings");
                intent.setComponent(component);
                intent.setAction("android.intent.action.VIEW");
            }
            startActivity(intent);
        }
    });
    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
        }
    });
builder.create();
builder.show();
}
}

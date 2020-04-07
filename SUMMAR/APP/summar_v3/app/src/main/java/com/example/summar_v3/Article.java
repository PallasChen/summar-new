package com.example.summar_v3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Article extends AppCompatActivity  {
    TextView article_tv,title_tv,date_tv,classificationbtn,tags_tv;
    ImageView imageView;
    Button next,back,citizen;
    ImageButton collection;
    String sa="",title="",classification="",date="",url="",pic="",keyword="",id="",collect="",citizen_id="";
    String useremail;
    ProgressDialog mDialog;
    AlertDialog dialog=null;
    private android.support.design.widget.TabLayout mTabs;
    private ViewPager mViewPager;
    JSONArray jsonArray;
    int i=0; //第一篇
    String result;
    String c,t;
    int flag = 0;
    ArrayList<String> collections=new ArrayList<>(); //存取使用者收藏
    public Handler mHander;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article);
        citizen=findViewById(R.id.citizenbtn);
        next=findViewById(R.id.b);
        back=findViewById(R.id.n);
        collection=findViewById(R.id.collectbtn);
        article_tv =findViewById(R.id.sa);
        title_tv =findViewById(R.id.title);
        date_tv = findViewById(R.id.date);
        classificationbtn = findViewById(R.id.classificationbtn);
        imageView=findViewById(R.id.iv);
        tags_tv=findViewById(R.id.tags);
        //讀取資料等待
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Loading ...");
        mDialog.setCancelable(false);
        //資料庫連線設定
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
        //使用者帳號（信箱)
        useremail=SharedPrefManger.getInstance(this).getUserEmail();

        //取得使用者收藏
        getData_collect();
        String temp[]=collect.split(",");
        if(temp[0].isEmpty()){collections.add(temp[0]);}//第一個空 不管怎樣都加
        for(int i=1;i<temp.length;i++) {
            if(temp[i].isEmpty()||temp[i].equals("")||temp[i]==null){} //如果空則不理
            else{
                collections.add(temp[i]);
            }
        }

        //收藏、搜尋傳過來title 標題
        t=getIntent().getStringExtra("ID");//收到對應的id，去取對應id摘要下來。
        //分類傳過來
        c = getIntent().getStringExtra("c");
        if(c!=null) {
            getData(c);//取資料(分類)
        }
        else if(t!=null){
            getData_title(t);//取資料
        }
        else{
        //首頁過來的資料，//首頁傳過來（最新）
        id=getIntent().getStringExtra("id");
        title=getIntent().getStringExtra("title");
        sa=getIntent().getStringExtra("sa");
        classification=getIntent().getStringExtra("classification");
        date=getIntent().getStringExtra("date");
        url=getIntent().getStringExtra("url");
        pic=getIntent().getStringExtra("pic");
        keyword=getIntent().getStringExtra("keyword");
        citizen_id=getIntent().getStringExtra("citizen_id");
        //最新顯現在畫面上
        onlayout(title,sa,classification,date,keyword);
        }
        //判斷是否有公共政策
        if(!citizen_id.isEmpty()){
            citizen.setVisibility(View.VISIBLE);
            //公民有話說按鈕
            citizen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater inflater = LayoutInflater.from(Article.this);
                    final View v = inflater.inflate(R.layout.dialog_citizen, null);

                    dialog = new AlertDialog.Builder(Article.this).create();
                    dialog.setView(v);

                    TextView title_citizen = v.findViewById(R.id.title);
                    TextView content_citizen =v.findViewById(R.id.content_citizen);
                    TextView affect_citizen=v.findViewById(R.id.affect_citizen);
                    Button url_citizen=v.findViewById(R.id.url);

                    //抓取資料
                    try {
                        result = DBConnector.executeQuery("sql=Select * From summar.citizen where id='"+citizen_id+"'");
                        jsonArray=new JSONArray(result);
                        final JSONObject obj = jsonArray.getJSONObject(0);
                        title_citizen.setText(obj.getString("title"));
                        content_citizen.setText(obj.getString("content"));
                        affect_citizen.setText(obj.getString("affect"));
                        final String url=obj.getString("url");
                        url_citizen.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setClass(Article.this , Web_url.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("url",url);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                    }catch (Exception e){}
                    dialog.setCancelable(false);//按对话框以外的地方不起作用。按返回键也不起作
                    dialog.show();
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.show_view); //設定圓角

                    ImageButton close=v.findViewById(R.id.close);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            });
        }else{
            citizen.setVisibility(View.GONE);
        }

        //判斷該文章是否被收藏
        for(int i=0;i<collections.size();i++){
            if(id.equals(collections.get(i))){ //已經存在
                flag=1;
                collection.setImageResource(R.drawable.pencil);
            }
        }


        mTabs =findViewById(R.id.tablayout);
        mTabs.addTab(mTabs.newTab().setText("最新"));
        mTabs.addTab(mTabs.newTab().setText("熱門"));

        mViewPager = findViewById(R.id.viewPager);
        mViewPager.setAdapter(new SamplePagerAdapter());

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs));
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
            collection.setOnClickListener(new Button.OnClickListener() {

                public void onClick(View v) {

                    switch(flag){
                        case 0:
                            if(!SharedPrefManger.getInstance(Article.this).isLogin()){
                                AlertDialog.Builder dialog = new AlertDialog.Builder(Article.this).setTitle("注意").setMessage("請先註冊才可使用此功能");
                                dialog.show();
                            }
                            else {
                                v = LayoutInflater.from(Article.this).inflate(R.layout.collect_toast, null);
                                new ToastUtil(Article.this, v, Toast.LENGTH_SHORT).show();
                                collection.setImageResource(R.drawable.pencil);
                                String collect = "";
                                for (int i = 0; i < collections.size(); i++) {
                                    collect =collect+collections.get(i) + ",";
                                }
                                for (int i = 0; i < collections.size(); i++) {
                                    if (collections.get(i).equals(id)) {}
                                    collect =collect+","+id; //加上最新的
                                }
                                putData_collect(collect); //上傳資料庫
                                flag = 1;
                                break;
                            }
                        case 1:
//                            if(!SharedPrefManger.getInstance(Article.this).isLogin()){
//                                AlertDialog.Builder dialog = new AlertDialog.Builder(Article.this).setTitle("注意").setMessage("請先註冊才可使用此功能");
//                                dialog.show();
//                            }
                                v = LayoutInflater.from(Article.this).inflate(R.layout.canel_toast, null);
                                new ToastUtil(Article.this, v, Toast.LENGTH_SHORT).show();
                                collection.setImageResource(R.drawable.pencill);
                                String collect = "";
                                collections.remove(id); //移除
                                if (collections.size() > 0) {
                                    for (int i = 0; i < collections.size() - 1; i++) {
                                        collect += collections.get(i) + ",";
                                    }
                                    collect += collections.get(collections.size() - 1);
                                    putData_collect(collect); //上傳資料庫
                                }
                                if (collections.size() == 0) {
                                    putData_collect(collect);
                                }
                                flag = 0;
                                break;

                    }
                }
            });

    }

    private void onlayout(String t,String s,String c,String d,String w) {
        mDialog.show();
        classificationbtn.setText(c);
        title_tv.setText(t);
        date_tv.setText(d);
        article_tv.setText(s);
        tags_tv.setText("#"+w);

        //上下篇的按鈕消失
        next.setVisibility(View.GONE);
        back.setVisibility(View.GONE);

        //圖片顯現
        if(pic==null || pic.isEmpty()){
            imageView.setImageResource(R.drawable.logo); //沒有圖片顯示的
        }
        else{
            Glide
                    .with(this)
                    .load(pic)
                    .skipMemoryCache(false)
                    .placeholder(R.drawable.loading_timage) //等待時顯現的圖片
                    .error(R.drawable.fail)//錯誤時顯現的圖片
                    //.centerCrop()
                    .fitCenter()
                    .into(imageView);
        }


        mDialog.dismiss(); //結束等待loading...
    }

    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Item " + (position + 1);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getLayoutInflater().inflate(R.layout.pager_item,
                    container, false);
            container.addView(view);
            TextView title = (TextView) view.findViewById(R.id.item_title);
            title.setText("");
            return view;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private void loadInto(String json) throws JSONException {
        jsonArray = new JSONArray(json);
        if(jsonArray.length()<=0){
            mDialog.dismiss(); //結束等待loading...
        }
        if(jsonArray.length()>0) {
            i=0; //最新的
            JSONObject obj = jsonArray.getJSONObject(0);
            id=obj.getString("id");
            title = obj.getString("title");
            classification = obj.getString("classification");
            date = obj.getString("date");
            url = obj.getString("url");
            pic = obj.getString("pic");
            sa = obj.getString("sa");
            keyword=obj.getString("keyword");

            classificationbtn.setText(classification);
            title_tv.setText(title);
            date_tv.setText(date);
            article_tv.setText(sa);
            tags_tv.setText("#"+keyword);

            //圖片顯現
            if(pic==null || pic.isEmpty()){
                imageView.setImageResource(R.drawable.logo);
            }
            else{
                Glide
                        .with(this)
                        .load(pic)
                        .skipMemoryCache(false)
                        .placeholder(R.drawable.loading_timage) //等待時顯現的圖片
                        .error(R.drawable.fail)//錯誤時顯現的圖片
                        //.centerCrop()
                        .fitCenter()
                        .into(imageView);
            }

            mDialog.dismiss(); //結束等待loading...
        }
    }
    public void back(View view) throws JSONException {
        if(i>0) {
            i--;
            JSONObject obj = jsonArray.getJSONObject(i);
            id=obj.getString("id");
            title = obj.getString("title");
            classification = obj.getString("classification");
            date = obj.getString("date");
            url = obj.getString("url");
            pic = obj.getString("pic");
            sa = obj.getString("sa");
            keyword=obj.getString("keyword");

            classificationbtn.setText(classification);
            title_tv.setText(title);
            date_tv.setText(date);
            article_tv.setText(sa);
            tags_tv.setText("#"+keyword);

            //************** 計算點擊次數
            getCount(classification);
            //**************

            //圖片顯現
            if(pic==null || pic.isEmpty()){
                imageView.setImageResource(R.drawable.logo);
            }
            else{
                Glide
                        .with(this)
                        .load(pic)
                        .skipMemoryCache(false)
                        .placeholder(R.drawable.loading_timage) //等待時顯現的圖片
                        .error(R.drawable.fail)//錯誤時顯現的圖片
                        //.centerCrop()
                        .fitCenter()
                        .into(imageView);
            }
        }
        else{i=0;}
    }
    public void next(View view) throws JSONException {
        if(i<jsonArray.length()-1) {
            i++;
            JSONObject obj = jsonArray.getJSONObject(i);
            id=obj.getString("id");
            title = obj.getString("title");
            classification = obj.getString("classification");
            date = obj.getString("date");
            url = obj.getString("url");
            pic = obj.getString("pic");
            sa = obj.getString("sa");
            keyword=obj.getString("keyword");

            classificationbtn.setText(classification);
            title_tv.setText(title);
            date_tv.setText(date);
            article_tv.setText(sa);
            tags_tv.setText("#"+keyword);

            //************** 計算點擊次數
            getCount(classification);
            //**************

            //圖片顯現
            if(pic==null || pic.isEmpty()){
                imageView.setImageResource(R.drawable.logo);
            }
            else{
                Glide
                        .with(this)
                        .load(pic)
                        .skipMemoryCache(false)
                        .placeholder(R.drawable.loading_timage) //等待時顯現的圖片
                        .error(R.drawable.fail)//錯誤時顯現的圖片
                        //.centerCrop()
                        .fitCenter()
                        .into(imageView);
            }
        }
        else{
            //載入更多
            i=jsonArray.length()-1;
        }
    }

    public void openurl(View view) {
        Intent intent = new Intent();
        intent.setClass(Article.this , Web_url.class);
        Bundle bundle = new Bundle();
        bundle.putString("url",url);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void getData(String c){
        try {
            result = DBConnector.executeQuery("sql=Select * From summar.original where classification='"+c+"' ORDER BY date DESC Limit 50 ");
            jsonArray=new JSONArray(result);
            //顯現在layout
            mDialog.show();
            loadInto(result);
        }catch (Exception e){

        }
    }
    public void getData_title(String t){
        try {
            result = DBConnector.executeQuery("sql=Select * From summar.original Where id='"+t+"' " +
                    "UNION Select * From summar.hot Where id='"+t+"' " +
                    "UNION Select * From summar.new Where id='"+t+"'");
            jsonArray=new JSONArray(result);
            //顯現在layout
            mDialog.show();
            //上下篇的按鈕消失
            next.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
            //顯示在layout
            loadInto(result);
        }catch (Exception e){

        }
    }
    public void getData_collect(){
        try {
            String result=DBConnector.executeQuery("sql=Select * From summar.member where email='"+useremail+"'");
            //String result=DBConnector.executeQuery("sql=Select * From summar.member");
            JSONArray jsonArray=new JSONArray(result);
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonData = jsonArray.getJSONObject(0);
                collect = jsonData.getString("collection");
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void putData_collect(String keyword){
        try {
              DBConnector.executeQuery("sql=UPDATE member SET collection ='"+keyword+"' WHERE email='"+useremail+"'");
        }catch (Exception e){
        }
    }
    //**************
    public void getCount(String classification){
        try {
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
            }
            String getNum=DBConnector.executeQuery("sql=Select "+c+" From member WHERE email='"+useremail+"'");
            JSONArray jsontemp;
            jsontemp=new JSONArray(getNum);
            JSONObject jsonData = jsontemp.getJSONObject(0);
            String temp=jsonData.getString(c);
            int Num=Integer.parseInt(temp);
            Num++;
            String saveNum=DBConnector.executeQuery("sql=UPDATE member SET "+c+"='"+Num+"'WHERE email='"+useremail+"'");
            jsontemp=new JSONArray(saveNum);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //**************
}

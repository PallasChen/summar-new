package com.example.summar_v3;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class Search extends AppCompatActivity implements View.OnClickListener{
    Button b1,b2,b3,b4,b5; //工具列按鈕
    SearchView searchView;
    ListView listView;//列表
    CustomAdapter mCustomAdapter;//列表與資料的接合器
    ArrayList<String> article_title=new ArrayList<>();
    ArrayList<String> article_pic=new ArrayList<>();
    ArrayList<String> article_id=new ArrayList<>();
    //**************
    ArrayList<String> article_cla=new ArrayList<>();
    //*************
    ArrayList<News> date = new ArrayList<>();
    ArrayList<News> date_all = new ArrayList<>();
    ArrayList<String> t=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("搜尋");
        setSupportActionBar(toolbar);

        //資料庫連線設定
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

        //定義工具列跟監聽
        b1=findViewById(R.id.bt1);
        b1.setOnClickListener(this);
        b2=findViewById(R.id.bt2);
        b2.setOnClickListener(this);
        b3=findViewById(R.id.bt3);
        b3.setBackgroundResource(R.drawable.searched);
//        b3.setOnClickListener(this);
        b4=findViewById(R.id.bt4);
        b4.setOnClickListener(this);
        b5=findViewById(R.id.bt5);
        b5.setOnClickListener(this);

        //定義
        searchView=findViewById(R.id.searchview);//搜尋位址
        listView=findViewById(R.id.listview);//列表位址
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(false);
        setSearch_function();//對searchview進行監聽

        //定義SearchView樣式
        SearchViewTheme(searchView);

        if(!SharedPrefManger.getInstance(this).isLogin()){ //快速登入，無使用者分析
            getData_all();

            for (int i = 0; i < article_pic.size(); i++) {
                News news = new News(); //製作單筆資料
                news.setTitle(article_title.get(i));
                news.setImageUrl(article_pic.get(i));
                news.setId(article_id.get(i));
                date_all.add(news); //把單筆資料加入陣列
            }

            mCustomAdapter = new CustomAdapter(this,date_all);
            listView.setAdapter(mCustomAdapter);
            listView.setTextFilterEnabled(true);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //-----當觸及項目是做啥動作？可以傳值過去Article跟做使用者分析
                    Intent intent = new Intent(Search.this, Article.class);
                    News news = (News) mCustomAdapter.getItem(i);
                    getCount(news.getClassification());
                    //********
                    intent.putExtra("ID", news.getId() + "");
                    startActivity(intent);
                }
            });
        }
        else {
            getData();//資料庫取資料

            //兩筆(title,pic_url)資料size()一樣才進入迴圈
            if (article_title.size() == article_pic.size()) {
                for (int i = 0; i < article_pic.size(); i++) {
                    News news = new News(); //製作單筆資料
                    news.setTitle(article_title.get(i));
                    news.setImageUrl(article_pic.get(i));
                    //************
                    news.setClassification(article_cla.get(i));
                    //************
                    news.setId(article_id.get(i));
                    date.add(news); //把單筆資料加入陣列
                }
            }

            mCustomAdapter = new CustomAdapter(this, date);
            listView.setAdapter(mCustomAdapter);
            listView.setTextFilterEnabled(true);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //-----當觸及項目是做啥動作？可以傳值過去Article跟做使用者分析
                    Intent intent = new Intent(Search.this, Article.class);
                    News news = (News) mCustomAdapter.getItem(i);
                    getCount(news.getClassification());
                    //********
                    intent.putExtra("ID", news.getId() + "");
                    startActivity(intent);
                }
            });
        }
    }


    private void setSearch_function() {//對searchview進行監聽
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            //當完成輸入內容點選搜尋按鈕時會回傳
            public boolean onQueryTextSubmit(String s) {
                mCustomAdapter.getFilter().filter(s);//s為我們在文字框打的文字，對Adpater內進行過濾
                return false;
            }

            @Override
            //當文字框每次內容發生改變時方法會回傳
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    public void SearchViewTheme(SearchView searchView){
        searchView.setBackgroundColor(Color.parseColor("#EDE9E5"));//背景顏色
        searchView.setQueryHint("Search");//提示顯示Hint
        searchView.setIconifiedByDefault(false);
    }

    public void getData(){
        //**************
        String useremail=SharedPrefManger.getInstance(this).getUserEmail();
        UserAnalyze ua=new UserAnalyze(useremail);
        String[]p=ua.getPercent().split(",");//取得比例
        String[]c=ua.getName().split(",");//取得分類
        double count=50;//預取得總筆數
        int count_num=0;
        Random r=new Random();
        try{
            for(int i=0;i<p.length;i++){
                //如果該分類有興趣項目為0時，直接將它視為1
                if(p[i].equals("0")){
                    p[i]="1";
                }
                int aa= (int) (count*Double.parseDouble(p[i])/100);
                String result=DBConnector.executeQuery("sql=Select * From summar.original WHERE classification='"+c[i]+"' ORDER BY date DESC LIMIT "+aa+"");
                JSONArray jsonArray=new JSONArray(result);
                for(int j=0;j<jsonArray.length();j++) {
                    JSONObject jsonData = jsonArray.getJSONObject(j);
                    article_title.add(jsonData.getString("title"));
                    article_pic.add(jsonData.getString("pic"));
                    article_cla.add(jsonData.getString("classification"));
                    article_id.add(jsonData.getString("id"));
                    count_num++;
                }
            }
            //如果今天不滿預設篇數時，以最有興趣文章來補滿
            if (count_num<(int)count){
                //先找出最有興趣的文章分類
                int max=Integer.parseInt(c[0]);
                int index=0;
                for(int i=0;i<c.length;i++){
                    if(Integer.parseInt(c[i])>max){
                        max=Integer.parseInt(c[i]);
                        index=i;
                    }
                }
                //將文章分類未達預設筆數的補插筆數進去
                int dist= (int) (count-count_num);
                String result=DBConnector.executeQuery("sql=Select * From summar.original WHERE classification='"+c[index]+"' ORDER BY date DESC LIMIT "+dist+"");
                JSONArray jsonArray=new JSONArray(result);
                for(int j=0;j<jsonArray.length();j++) {
                    JSONObject jsonData = jsonArray.getJSONObject(j);
                    article_title.add(jsonData.getString("title"));
                    article_pic.add(jsonData.getString("pic"));
                    article_cla.add(jsonData.getString("classification"));
                    article_id.add(jsonData.getString("id"));
                }
            }
            //亂數處理
            for (int k=0;k<(int)count*3;k++){
                int m=r.nextInt((int)count);
                int n=r.nextInt((int)count);
                String temp_title=article_title.get(m);
                String temp_pic=article_pic.get(m);
                String temp_cla=article_cla.get(m);
                String temp_id=article_id.get(m);
                article_title.set(m,article_title.get(n));
                article_pic.set(m,article_pic.get(n));
                article_cla.set(m,article_cla.get(n));
                article_id.set(m,article_id.get(n));
                article_title.set(n,temp_title);
                article_pic.set(n,temp_pic);
                article_cla.set(n,temp_cla);
                article_id.set(n,temp_id);
                t.add(m+","+n);
            }
        }catch (Exception e){

        }
        //***************
    }
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
    public void getData_all() {
        try {
            String result=DBConnector.executeQuery("sql=Select * From summar.original ORDER BY date DESC Limit 50");
            JSONArray jsonArray=new JSONArray(result);
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                article_title.add(jsonData.getString("title"));
                article_pic.add(jsonData.getString("pic"));
                article_id.add(jsonData.getString("id"));
            }
        }catch (Exception e){
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.bt1:
                // code for button when user clicks buttonOne.
                intent.setClass(this, home.class);
                startActivity(intent);
                break;
            case R.id.bt2:
                // do your code
                intent.setClass(this,classification.class);
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

}

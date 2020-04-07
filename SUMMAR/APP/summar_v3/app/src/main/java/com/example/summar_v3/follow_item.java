package com.example.summar_v3;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class follow_item extends AppCompatActivity {
    String key;
    ListView listView;//列表
    ArrayList<String> article_title=new ArrayList<>(); //標題
    ArrayList<String> article_pic=new ArrayList<>(); //圖片
    ArrayList<String> article_sa=new ArrayList<>(); //文章內容
    ArrayList<String> article_keyword=new ArrayList<>(); //關鍵字
    ArrayList<String> article_id=new ArrayList<>(); //id
    ArrayList<News> date = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_item);
        //key
        key = this.getIntent().getStringExtra("key");
        //工具列toolbar
        Toolbar toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle(key);
        setSupportActionBar(toolbar);
        listView=findViewById(R.id.listview);//列表位址
        //資料庫連線設定
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
        getData(key);//資料庫取資料
        //放入News陣列
        for(int i = 0; i<article_pic.size();i++){
            News news = new News(); //製作單筆資料
            news.setTitle(article_title.get(i));
            news.setImageUrl(article_pic.get(i));
            news.setKeyword(article_keyword.get(i));
            news.setSummary(article_sa.get(i));
            news.setId(article_id.get(i));
            date.add(news); //把單筆資料加入陣列
        }
        final follow_item_adapter followItemAdapter=new follow_item_adapter(this,date,key);
        listView.setAdapter(followItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(follow_item.this, Article.class);
                News news = (News) followItemAdapter.getItem(position);
                Toast.makeText(follow_item.this,news.getTitle()+"", Toast.LENGTH_SHORT).show();
                //intent.putExtra("ID",article_id.get(i));
                intent.putExtra("ID",news.getId()+"");
                startActivity(intent);
            }
        });
        //數量、筆數
        TextView count = findViewById(R.id.count);
        count.setText("有"+followItemAdapter.getCount()+"筆");

    }
    public void getData(String key){
            String result=DBConnector.executeQuery("sql=Select * From summar.original where title like '%"+key+"%' ORDER BY date DESC"); //取文章列表
                JSONArray jsonArray= null;
                try {
                    jsonArray = new JSONArray(result);
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        article_title.add(jsonData.getString("title"));
                        article_pic.add(jsonData.getString("pic"));
                        article_keyword.add(jsonData.getString("keyword"));
                        article_sa.add(jsonData.getString("sa"));
                        article_id.add(jsonData.getString("id"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
    }

}

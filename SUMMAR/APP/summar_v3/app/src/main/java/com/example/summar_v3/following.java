package com.example.summar_v3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class following extends AppCompatActivity implements View.OnClickListener{
    ListView listView;//列表
    ArrayList<String> tags = new ArrayList<>(); //存tags（要追蹤的關鍵字、議題）的地方
    String useremail,key;
    ImageView unfollow;
    Button b1,b2,b3,b4,b5; //工具列按鈕

    private MyAdapter myAdapter;
    private Set<SlideLayout> sets = new HashSet(); //儲存暫存的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.following);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("追蹤");
        setSupportActionBar(toolbar);
        //定義工具列跟監聽
        b1 = findViewById(R.id.bt1);
        b1.setOnClickListener(this);
        b2 = findViewById(R.id.bt2);
        b2.setOnClickListener(this);
        b3 = findViewById(R.id.bt3);
        b3.setOnClickListener(this);
        b4 = findViewById(R.id.bt4);
//        b4.setOnClickListener(this);
        b4.setBackgroundResource(R.drawable.followww);
        b5 = findViewById(R.id.bt5);
        b5.setOnClickListener(this);

        //無追蹤背景
        unfollow = findViewById(R.id.unfollow);

        //使用者帳號（信箱)
        useremail = SharedPrefManger.getInstance(this).getUserEmail();

        //判斷是否為訪客-->有登入
        if (SharedPrefManger.getInstance(this).isLogin()) {


            //資料庫連線設定
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
            //
            listView = findViewById(R.id.listview);//列表位址

            getData(); // 取得使用者的following
            if (key == null || key.isEmpty()) {
            } else {
                String keys[] = key.split(",");
                for (int i = 0; i <keys.length; i++) {
                    if (keys[i].isEmpty() || keys[i].equals("") || keys[i] == null) {
                    }//如果為空則不存入
                    else {
                        tags.add(keys[i]);
                    }
                }
            }
            //判斷是否有追蹤
            if (tags.size() > 0) {
                unfollow.setVisibility(View.GONE);
            } else {
                unfollow.setVisibility(View.VISIBLE);
            }
            myAdapter = new MyAdapter(this, tags);
            listView.setAdapter(myAdapter);
        }
        else{

        }
    }
    class MyAdapter extends BaseAdapter
    {
        private Context content;
        private ArrayList<String> datas;
        private MyAdapter(Context context, ArrayList<String> datas)
        {
            this.content = context;
            this.datas = datas;
        }
        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder=null;
            if (convertView == null)
            {
                convertView = LayoutInflater.from(content).inflate(R.layout.item_following, null);
                viewHolder = new ViewHolder();
                viewHolder.contentView= (TextView) convertView.findViewById(R.id.content_citizen);
                viewHolder.menuView = (TextView) convertView.findViewById(R.id.menu);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.contentView.setText(datas.get(position));

            viewHolder.contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),follow_item.class);
                String key = (String) ((TextView)v).getText();
                intent.putExtra("key",key);
                startActivity(intent);
                }
            });
            final String myContent = datas.get(position);
            viewHolder.menuView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SlideLayout slideLayout = (SlideLayout) v.getParent();
                    slideLayout.closeMenu(); //解决删除item后下一个item变成open状态问题
                    datas.remove(myContent);
                    notifyDataSetChanged();
                    //更新資料庫中的關鍵字
                    tags.remove(myContent);
                    String keyword = "";
                    if(tags.size()>0) { //大於0時
                        unfollow.setVisibility(View.GONE);
                        for (int i = 0; i < tags.size() - 1; i++) {
                            keyword += tags.get(i) + ",";
                        }
                        keyword += tags.get(tags.size() - 1); //最後一個
                        putData(keyword);
                        }
                    if(tags.size()==0){     //傳入空
                        putData(keyword);
                        unfollow.setVisibility(View.VISIBLE);}
                    }
            });

            SlideLayout slideLayout = (SlideLayout) convertView;
            slideLayout.setOnStateChangeListener(new MyAdapter.MyOnStateChangeListener());


            return convertView;
        }

        public SlideLayout slideLayout = null;
        class MyOnStateChangeListener implements SlideLayout.OnStateChangeListener
        {
            /**
             * 滑动后每次手势抬起保证只有一个item是open状态，加入sets集合中
             **/
            @Override
            public void onOpen(SlideLayout layout) {
                slideLayout = layout;
                if (sets.size() > 0) {
                    for (SlideLayout s : sets) {
                        s.closeMenu();
                        sets.remove(s);
                    }
                }
                sets.add(layout);
            }

            @Override
            public void onMove(SlideLayout layout) {
                if (slideLayout != null && slideLayout !=layout)
                {
                    slideLayout.closeMenu();
                }
            }

            @Override
            public void onClose(SlideLayout layout) {
                if (sets.size() > 0) {
                    sets.remove(layout);
                }
                if(slideLayout ==layout){
                    slideLayout = null;
                }
            }
        }
    }
    static class ViewHolder
    {
        public TextView contentView;
        public TextView menuView;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                if(!SharedPrefManger.getInstance(this).isLogin()){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("注意").setMessage("請先註冊才可使用此功能");
                    dialog.show();
                }
                else {
//                加入關鍵字
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("關注的話題");
                    //edittext
                    final EditText input = new EditText(this);
                    dialog.setView(input);
                    //確定
                    dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String keyword = input.getText().toString();
                            unfollow.setVisibility(View.GONE);
                            tags.add(keyword);
                            // 存入資料庫
                            keyword = "";
                            for (int i = 0; i < tags.size() - 1; i++) {
                                keyword += tags.get(i) + ",";
                            }
                            keyword += tags.get(tags.size() - 1); //最後一個
                            putData(keyword);
                        }
                    });
                    //取消
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setCancelable(false);
                    dialog.show();
                    return true;
                }


                    default:
                        return super.onOptionsItemSelected(item);

        }
    }
    public void putData(String keyword){
        try {
            DBConnector.executeQuery("sql=UPDATE member SET following='"+keyword+"' WHERE email='"+useremail+"'");
        }catch (Exception e){
        }
    }
    public void getData(){
        try {
            String result=DBConnector.executeQuery("sql=Select * From summar.member where email='"+useremail+"'");
            //String result=DBConnector.executeQuery("sql=Select * From summar.member");
            JSONArray jsonArray=new JSONArray(result);
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonData = jsonArray.getJSONObject(0);
                key = jsonData.getString("following");
            }
        }catch (JSONException e) {
            e.printStackTrace();
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
            case R.id.bt3:
                // do your code
//                startActivity(intent);
                intent.setClass(this,Search.class);
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

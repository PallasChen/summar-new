package com.example.summar_v3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class follow_item_adapter extends BaseAdapter {
    Context context;
    ArrayList<News> datalists; //全部的文章
    String key; //要對照的關鍵字
    private LayoutInflater mLayout;

    public follow_item_adapter(Context context, ArrayList<News> datalists,String key){
        mLayout=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context=context;
        this.datalists=datalists;
        this.key=key;
    }

    @Override
    public int getCount() {
        if(datalists.size()>0){
        return datalists.size();
    }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return datalists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=mLayout.inflate(R.layout.listcontent_layout,parent,false);
        News news = datalists.get(position);

        TextView title=v.findViewById(R.id.textView1);
        title.setText(news.getTitle());
        ImageView image =v.findViewById(R.id.imageView2);
        //圖片顯現
        String pic = news.getImageUrl();
        if(pic==null || pic.isEmpty()){
            image.setImageResource(R.drawable.logo);
        }
        else{
            Glide
                    .with(context)
                    .load(pic)
                    .skipMemoryCache(false)
                    .placeholder(R.drawable.loading_timage) //等待時顯現的圖片
                    .error(R.drawable.fail)//錯誤時顯現的圖片
                    //.centerCrop()
                    .fitCenter()
                    .into(image);
        }
        return v;
    }
}

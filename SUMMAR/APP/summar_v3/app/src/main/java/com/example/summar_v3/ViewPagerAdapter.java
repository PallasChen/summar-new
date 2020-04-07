package com.example.summar_v3;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    List<News> list; //存放新聞的地方
    private Integer[] images = {R.drawable.fail,R.drawable.fail,R.drawable.fail,R.drawable.fail,R.drawable.fail};

    public ViewPagerAdapter(Context context){
        this.context = context;
    }
    public ViewPagerAdapter(Context context,List<News> list){
        this.context = context;
        this.list=list;
    }

    @Override
    public int getCount(){
        //因為要實現輪播，所以將數值設置的大一些
        return Integer.MAX_VALUE;
    }
    @Override
    public boolean isViewFromObject(View view, Object object){
        return view==object;
    }
    @Override
    public Object instantiateItem(ViewGroup container,int position){
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.sideshow_layout,null);

            final int realPosition = position % images.length;
            if(list.size()<=0){}
            else {
                News news = list.get(realPosition);
                //    設定背景圖片
                TextView tv = view.findViewById(R.id.textwhite); //標題
                String title = news.getTitle();
                tv.setText(title);
                ImageView imageView =view.findViewById(R.id.iv);
                imageView.setImageResource(images[realPosition]);
                //圖片顯現
                String pic = news.getImageUrl();
                if (pic == null || pic.isEmpty()) {
                    imageView.setImageResource(R.drawable.noimage);
                } else {
                    Glide
                            .with(context)
                            .load(pic)
                            .skipMemoryCache(false)
                            .placeholder(R.drawable.loading_timage) //等待時顯現的圖片
                            .error(R.drawable.fail)//錯誤時顯現的圖片
                            //.centerCrop()
                            .fitCenter()
                            .into(imageView);
                }

                // 为每页添加点击监听
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        News news = list.get(realPosition);
                        Intent intent = new Intent(context, Article.class);
                        intent.putExtra("id",news.getId());
                        intent.putExtra("title", news.getTitle());
                        intent.putExtra("sa", news.getSummary());
                        intent.putExtra("classification", news.getClassification());
                        intent.putExtra("date", news.getDate());
                        intent.putExtra("url", news.getUrl());
                        intent.putExtra("pic", news.getImageUrl());
                        intent.putExtra("keyword",news.getKeyword());
                        intent.putExtra("citizen_id",news.getCitizen_id());
                        context.startActivity(intent);
                    }
                });
            }
        ViewPager vp = (ViewPager)container;
        vp.addView(view);
        return view;
    }
    @Override
    public void destroyItem(ViewGroup container,int position,Object object){
        ViewPager vp = (ViewPager)container;
        View view = (View) object;
        vp.removeView(view);
    }
}

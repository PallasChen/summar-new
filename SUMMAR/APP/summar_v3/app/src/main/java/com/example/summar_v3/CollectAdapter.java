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

public class CollectAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    List<News> list; //存放新聞的地方

    public CollectAdapter(Context context, List<News> list){
        this.context = context;
        this.list=list;
    }

    @Override
    public int getCount(){
        return list.size();
    }
    @Override
    public boolean isViewFromObject(View view, Object object){
        return view==object;
    }
    @Override
    public Object instantiateItem(ViewGroup container,int position){
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.collection_item,null);

        final News news = list.get(position);

        TextView title =view.findViewById(R.id.title);
        title.setText(news.getTitle());

        ImageView imageView = view.findViewById(R.id.imageview); // 圖片
        ImageView white = view.findViewById(R.id.white); // 背景

//        imageView.setImageResource(images[position]);
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
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Article.class);
                //intent.putExtra("ID",article_id.get(i));
                intent.putExtra("ID",news.getId()+"");
                context.startActivity(intent);
            }
        });
        white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Article.class);
                //intent.putExtra("ID",article_id.get(i));
                intent.putExtra("ID",news.getId()+"");
                context.startActivity(intent);
            }
        });
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Article.class);
                //intent.putExtra("ID",article_id.get(i));
                intent.putExtra("ID",news.getId()+"");
                context.startActivity(intent);
            }
        });


        ViewPager vp = (ViewPager)container;
        vp.addView(view,0);

        return view;
    }
    @Override
    public void destroyItem(ViewGroup container,int position,Object object){
        ViewPager vp = (ViewPager)container;
        View view = (View) object;
        vp.removeView(view);
    }
}

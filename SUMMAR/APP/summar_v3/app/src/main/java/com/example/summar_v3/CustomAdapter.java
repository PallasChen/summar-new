package com.example.summar_v3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//搜尋...
public class CustomAdapter extends BaseAdapter implements Filterable {
    Context context;
    private ArrayList<News> datalists;//是會變動的陣列，用來顯示正個recyclerView的資料
    private LayoutInflater mLayout;

    public CustomAdapter(Context context, ArrayList<News> datalists){
        mLayout=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context=context;
        this.datalists=datalists;
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
    public View getView(int position, View view, ViewGroup viewGroup) {//取得list框框內容
        View v=mLayout.inflate(R.layout.listcontent_layout,viewGroup,false);
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


    @Override
    public Filter getFilter() {//建議
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                charSequence = charSequence.toString();
                FilterResults result = new FilterResults();

//                if (filterDatas == null) {
//                    filterDatas = new ArrayList<News>(datalists);
//                }

                ArrayList<News> filterData = new ArrayList<>();

                //先判斷filterContent是不是null才進入
                if (charSequence != null && charSequence.toString().length() > 0) {
                    //這回圈是在判斷你輸入的文字（filterContent）是否有在filterDatas陣列內有相關的文字，逐條搜尋。
                    String s=String.valueOf(charSequence);
                        String re = DBConnector.executeQuery("sql=Select * From summar.original where title like '%" + s + "%' ORDER BY date DESC "); //取文章列表
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(re);
                            for (int i = 0; i <jsonArray.length(); i++) {
                                JSONObject jsonData = jsonArray.getJSONObject(i);
                                News news = new News();
                                news.setTitle( jsonData.getString("title"));
                                news.setImageUrl(jsonData.getString("pic"));
                                news.setKeyword(jsonData.getString("keyword"));
                                news.setSummary(jsonData.getString("sa"));
                                news.setId(jsonData.getString("id"));
                                filterData.add(news);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                    }
                    result.count = filterData.size();
                    result.values = filterData;
                } else {
                    //確認什麼都沒打
                    filterData = datalists;
                    result.values = filterData;
                    result.count = filterData.size();
                }

                return result;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                datalists = (ArrayList<News>) filterResults.values;
                if (filterResults.count > 0) {
                    notifyDataSetChanged();//通知view資料改變
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

}

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
import java.util.List;

public class Myadapter extends BaseAdapter {
//    private static final int TYPE_MOREITEM = 0; //加载更多对应的Item
//    private static final int TYPE_CONTENTITEM = TYPE_MOREITEM + 1; //普通的item类型
    Context context;
    List<News> list ;
    Myadapter(Context context){
        super();
        this.context=context;
        //離線版
        list=new ArrayList<News>();
        list.add(new News("udn_20191016_I_1",
                "",
                "金正恩身騎白馬登白頭山 暗示將有「重大決策」",
                "北韓官媒《朝中社》16日報導，北韓國家領導人金正恩「迎著白頭山的第一場雪親自騎白馬登上白頭山。」，踏上「在北韓革命史上具有重大意義」的白頭山行軍路，堅信他將繪製「北韓革命將要取得跨越式發展的驚人作戰鴻圖」，當您看到優質新聞，即可點按文章中的「贊助好新聞」按鈕贊助該篇文章，點按文章中的「贊助好新聞」，以活動金幣贊助該篇文章，點按文章中的「贊助好新聞」。",
                "https://udn.com/news/story/6809/4107876",
                "2019-10-16","全球",
                "北韓,贊助,新聞",""
        ));
        list.add(new News("udn_20191016_I_3",
                "",
                "炸彈阿富汗警局外爆炸 2警殉職20童傷",
                "2019年10月21日 - 2019年11月20日活動期間您可獲得活動金幣 3,000 枚，即可點按文章中的「贊助好新聞」按鈕贊助該篇文章，點按文章中的「贊助好新聞」，以活動金幣贊助該篇文章，點按文章中的「贊助好新聞」，以活動金幣贊助該篇文章。",
                "https://udn.com/news/story/6809/4107746",
                "2019-10-16","全球",
                "贊助,文章,新聞,活動,金幣,阿富汗",""));
        list.add(new News("udn_20191016_I_2",
                "",
                "東協中國官員會議 越南控中國船隻違反主權",
                "第18屆南海共同行為宣言（DOC）的東協與中國資深官員會議昨天在越南大叻市舉行，即可點按文章中的「贊助好新聞」按鈕贊助該篇文章，點按文章中的「贊助好新聞」，不利於「南海行為準則（COC）」的談判進程，達成「南海行為準則」對南海當前與未來類似緊張局勢的迫切性，表示希望南海行為準則談判進程能儘快完成，應繼續確保充分與有效落實「南海共同行為宣言」，同時加大建立「南海行為準則」的力度。",
                "https://udn.com/news/story/6809/4107880",
                "2019-10-1","全球",
                "南海,贊助,行為",""));
        list.add(new News("udn_20191016_I_4",
                "https://pgw.udn.com.tw/gw/photo.php?u=https://uc.udn.com.tw/photo/2019/10/16/anntw/6947842.jpg&x=0&y=0&sw=0&sh=0&exp=3600",
                "加獨領袖遭判刑 巴塞陷入動亂",
                "加泰隆尼亞獨立聲浪再起，近日領導加泰隆尼亞獨立的領袖群遭到判刑，加泰隆尼亞大城巴塞隆納出現許多抗議人潮，這也讓西班牙最敏感的政治議題再度躍上檯面，西班牙將於11月再度舉行大選，2年前領導加泰隆尼亞獨立公投的9位領袖近日遭西班牙最高法院以分離罪判刑，其中以加泰隆尼亞共和左翼主席容克拉斯判刑最重，西班牙這樣做只會讓加獨的聲浪擴大，他呼籲西班牙政府要儘速跟加泰隆尼亞政府展開對話。",
                "https://udn.com/news/story/6809/4107719",
                "2019-10-16","全球",
                "西班牙,加泰隆尼亞,贊助",""));
        list.add(new News("udn_20191016_I_5",
                "https://pgw.udn.com.tw/gw/photo.php?u=https://uc.udn.com.tw/photo/2019/10/16/anntw/6947843.jpg&x=0&y=0&sw=0&sh=0&exp=3600",
                "敘、土情勢一觸即發 俄國極力勸阻",
                "美國近日將軍隊撤出敘利亞北部後，土耳其便希望能掃蕩庫德族軍隊，因敘利亞近日表態支持庫德族軍隊，作為敘利亞最堅實的盟友，撤出的川普讓以往親美的庫德族卻向親俄的敘利亞靠攏，駐敘利亞俄國大使表示，絕對不允許土耳其對庫德族的軍事行為，目前俄國正幫助敘利亞政府與庫德族進行協議，用庫德族領土換取敘利亞的軍事支援，土耳其目前仍把「敘利亞民主力量」（庫德族最大武裝力量）視為恐怖組織。",
                "https://udn.com/news/story/6809/4107720",
                "2019-10-16","全球",
                "敘利亞,庫德族,贊助",""));
        list.add(new News("udn_20191016_I_6",
                "https://pgw.udn.com.tw/gw/photo.php?u=https://uc.udn.com.tw/photo/2019/10/16/anntw/6947844.jpg&x=0&y=0&sw=0&sh=0&exp=3600",
                "雖聲援反送中 英警逮捕千餘抗爭者",
                "環保團體「反抗滅絕」應停止在倫敦的抗爭活動，這些氣候變遷抗議人士在倫敦政府大樓和主要金融機構，以活動金幣贊助該篇文章，以活動金幣贊助該篇文章，儘管警方在倫敦範圍內禁止了「反抗滅絕」活動，倫敦市長薩迪克·汗則說：「我相信和平與合法抗議的權利必須始終得到維護。」，「抗議活動不應破壞人們的日常生活，政府發言人這麼說，在根據《公共秩序法》第14條宣布了新禁令之後，警察於14日晚間開始從特拉法加廣場清除抗議者，該禁令要求「活動份子在倫敦中部時間21點前停止抗議活動，否則有可能被捕。」。",
                "https://udn.com/news/story/6809/4107722",
                "2019-10-16","全球",
                "活動,贊助,倫敦,抗議",""));
        list.add(new News("udn_20191016_I_7",
                "https://pgw.udn.com.tw/gw/photo.php?u=https://uc.udn.com.tw/photo/2019/08/27/98/6743684.jpg&x=0&y=0&sw=0&sh=0&exp=3600",
                "歐盟高峰會在即 馬克宏梅克爾先會面協調立場",
                "法國總統馬克宏今天將和德國總理梅克爾會談，2019年10月21日 - 2019年11月20日活動期間您可獲得活動金幣 3,000 枚，即可點按文章中的「贊助好新聞」按鈕贊助該篇文章，點按文章中的「贊助好新聞」，以活動金幣贊助該篇文章，點按文章中的「贊助好新聞」，以活動金幣贊助該篇文章。。",
                "https://udn.com/news/story/6809/4107660",
                "2019-10-16","全球",
                "贊助,文章,新聞,活動,金幣,馬克宏",""));
        list.add(new News("udn_20191016_I_8",
                "",
                "NHK：北韓船員疑在能登半島附近落海",
                "2019年10月21日 - 2019年11月20日活動期間您可獲得活動金幣 3,000 枚，即可點按文章中的「贊助好新聞」按鈕贊助該篇文章，點按文章中的「贊助好新聞」，以活動金幣贊助該篇文章，點按文章中的「贊助好新聞」，以活動金幣贊助該篇文章。",
                "https://udn.com/news/story/6809/4107689",
                "2019-10-16","全球",
                "贊助,文章,新聞,日本,活動,金幣",""));
        list.add(new News("udn_20191016_I_9",
                "https://pgw.udn.com.tw/gw/photo.php?u=https://uc.udn.com.tw/photo/2019/10/16/6/6947093.jpg&x=0&y=0&sw=0&sh=0&exp=3600",
                "英願讓步 歐盟峰會前有望達脫歐協議",
                "英國和歐盟的脫歐談判代表16日凌晨挑燈開會，試圖敲定英國脫歐協議，他們可能會在17、18日歐盟峰會前的最後一刻達成協議，英國首相強生將自己的政治前途壓在帶領國家於10月31日前離開歐盟，但目前仍不清楚是否能趕在歐盟高峰會前達成協議，即使強生與歐盟達成協議，北愛將跟著英國離開歐盟，「即使英國與歐盟的脫歐協議越來越困難但本周仍可能達成。」。",
                "https://udn.com/news/story/6809/4107296",
                "2019-10-16","全球",
                "歐盟,英國,贊助,協議",""));
        list.add(new News("udn_20191016_I_10",
                "https://pgw.udn.com.tw/gw/photo.php?u=https://uc.udn.com.tw/photo/2019/10/16/6/6947098.jpg&x=0&y=0&sw=0&sh=0&exp=3600",
                "時機敏感 美6罪名起訴土國營銀行",
                "2019年10月21日 - 2019年11月20日活動期間您可獲得活動金幣 3,000 枚，即可點按文章中的「贊助好新聞」按鈕贊助該篇文章，點按文章中的「贊助好新聞」，以活動金幣贊助該篇文章，點按文章中的「贊助好新聞」，以活動金幣贊助該篇文章，這項起訴可能會被外界視為美國因應土耳其進攻敘利亞北部的政治攻擊，他曾施壓美國前總統歐巴馬及美國現任總統川普出面干預。。",
                "https://udn.com/news/story/6809/4107297",
                "2019-10-16","全球",
                "美國,贊助,川普,文章,新聞,土國,土耳其,活動,金幣",""));
    }
    Myadapter(Context context, List<News>list){
        super();
        this.context=context;
        this.list=list;
    }
    public void addListToadapter(List<News> mlist){
        //add list to current list to data
        list.addAll(mlist);
        //Notify UI
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,null);
        ImageView imageView_iv=(ImageView)convertView.findViewById(R.id.iv);
        TextView title_tv=(TextView)convertView.findViewById(R.id.title);
        TextView des_tv=(TextView)convertView.findViewById(R.id.des);

        News news = list.get(position);

        title_tv.setText(news.getTitle());
        des_tv.setText(news.getSummary());

        //圖片顯現
        String pic = news.getImageUrl();
        if(pic==null || pic.isEmpty()){
            imageView_iv.setImageResource(R.drawable.noimage);
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
                    .into(imageView_iv);
        }

        //

        return convertView;
    }
}

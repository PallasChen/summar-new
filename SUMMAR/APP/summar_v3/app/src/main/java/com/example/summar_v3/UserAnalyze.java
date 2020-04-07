package com.example.summar_v3;

import org.json.JSONArray;
import org.json.JSONObject;

public class UserAnalyze {
    double global=0;
    double politics=0;
    double digital=0;
    double business=0;
    double sport=0;
    double life=0;
    double edu=0;

    UserAnalyze(){
       //如果沒有傳使用者處理
    }
    UserAnalyze(String useremail){
        //如果有使用者傳來處理
        try {
            String getNum=DBConnector.executeQuery("sql=Select * From member WHERE email='"+useremail+"'");
            JSONArray jsontemp=new JSONArray(getNum);
            JSONObject jsonData = jsontemp.getJSONObject(0);
            this.global= (double) jsonData.getInt("Global");
            this.politics=(double)jsonData.getInt("Politics");
            this.digital=(double)jsonData.getInt("Digital");
            this.business=(double)jsonData.getInt("Business");
            this.sport=(double)jsonData.getInt("Sport");
            this.life=(double)jsonData.getInt("Life");
            this.edu=(double)jsonData.getInt("Edu");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public String getResult(){
        String temp="";
        double total=this.global+this.politics+this.digital+this.business+this.sport+this.life;
        temp="全球"+(int)(this.global/total*100)+"\n政治"+(int)(this.politics/total*100)+"\n數位"+(int)(this.digital/total*100)+"\n產經"+(int)(this.business/total*100)+"\n運動"+(int)(this.sport/total*100)+"\n生活"+(int)(this.life/total*100)+"\n教育"+(int)(this.edu/total*100);
        return temp;
    }
    public String getPercent(){
        String temp="";//百分比
        double total=this.global+this.politics+this.digital+this.business+this.sport+this.life+this.edu;
        temp=(int)(this.global/total*100)+","+(int)(this.politics/total*100)+","+(int)(this.digital/total*100)+","+(int)(this.business/total*100)+","+(int)(this.sport/total*100)+","+(int)(this.life/total*100)+","+(int)(this.edu/total*100);
        return temp;
    }
    public String getName(){
        String temp="";
        temp ="全球,政治,數位,產經,運動,生活,教育";//類別
        return temp;
    }
}

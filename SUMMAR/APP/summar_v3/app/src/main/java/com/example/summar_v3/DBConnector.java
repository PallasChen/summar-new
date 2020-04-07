package com.example.summar_v3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class DBConnector {
    public static String executeQuery(String query){
        String result="";
        try{
            URL url=new URL("http://127.0.0.1/conn.php");
            //URL url=new URL("http://192.168.31.194/GetData.php");
            HttpURLConnection con=(HttpURLConnection)url.openConnection();
            StringBuilder sb=new StringBuilder();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            OutputStream outputStream=con.getOutputStream();
            BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
//            String post_sql= URLEncoder.encode(query,"utf-8");
//            String sql="sql=Select * From summar.original";
            bw.write(query);//傳sql語法過去php
            bw.flush();
            bw.close();
            InputStream inputStream=con.getInputStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            String line="";
            while ((line=br.readLine())!=null){
                sb.append(line+"\n");
            }
            br.close();
            inputStream.close();
            con.disconnect();
            result=sb.toString();
        }catch (Exception e){
            //do nothing
        }
        return result;
    }
}

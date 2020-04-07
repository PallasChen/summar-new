package com.example.summar_v3;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class BackgroundWorker extends AsyncTask<String,Void,String> {
    Context context;
    BackgroundWorker(Context ctx){context=ctx;}
    String useremail;
    String password;
    @Override
    protected String doInBackground(String... params) {
        String type=params[0];
        String result="";
        String login_url = "http://163.13.201.70:8081/login_test.php";
        String register_url = "http://163.13.201.70:8081/register_test.php";

        if(type.equalsIgnoreCase("login")){
            try {
                useremail = params[1];
                password=params[2];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String post_data= URLEncoder.encode("user_email","UTF-8")+"="+URLEncoder.encode(useremail,"UTF-8")
                        +"&"+URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                bw.write(post_data);
                bw.flush();
                bw.close();
                outputStream.close();
                //==========================
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                String line="";
                while((line=br.readLine())!=null){
                    result += line;
                }
                br.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;

            } catch (MalformedURLException e) {
                result=e.getMessage();
            } catch (IOException e) {
                result=e.getMessage();
            }
        }
        else if(type.equalsIgnoreCase("register")){
            try {
                String username = params[1];
                String password=params[2];
                String email = params[3];
                String gender=params[4];
                String birth = params[5];
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String post_data= URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(username,"UTF-8")
                        +"&"+URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8")
                        +"&"+URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")
                        +"&"+URLEncoder.encode("gender","UTF-8")+"="+URLEncoder.encode(gender,"UTF-8")
                        +"&"+URLEncoder.encode("birth","UTF-8")+"="+URLEncoder.encode(birth,"UTF-8");
                bw.write(post_data);
                bw.flush();
                bw.close();
                outputStream.close();
                //==========================
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                String line="";
                while((line=br.readLine())!=null){
                    result += line;
                }
                br.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;

            } catch (MalformedURLException e) {
                result=e.getMessage();
            } catch (IOException e) {
                result=e.getMessage();
            }
        }
        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        if(result.contains("Login Success!")){
            try {
                JSONObject obj=new JSONObject(result);
                //登陸
                SharedPrefManger.getInstance(context).userlogin(
                        obj.getString("username"),
                        obj.getString("email")
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(context,home.class);
            intent.putExtra("isFill","1");  //啟動app 才會顯示問卷
            context.startActivity(intent);
            Toast.makeText(context,"登入成功",Toast.LENGTH_SHORT).show();
        }
        else if(result.equals("Login Failed!")){
            Toast.makeText(context,"登入失敗",Toast.LENGTH_SHORT).show();
        }
        else if(result.equals("Register Successful")){
            Toast.makeText(context,"註冊成功",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(context,MainActivity.class);
            context.startActivity(intent);
        }
        else if(result.equals("aleady")){
            Toast.makeText(context,"註冊失敗"+"，已註冊",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}

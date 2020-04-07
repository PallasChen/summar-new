package com.example.summar_v3;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManger {
    private static SharedPrefManger mInstance;
    private static Context mCtx;


    private static final String SHARED_NAME="userInfo";
    private static final String KEY_ID="userid";
    private static final String KEY_NAME="username";
    private static final String KEY_EMAIL="useremail";

    private SharedPrefManger(Context context){
        mCtx=context;
    }
    public static synchronized SharedPrefManger getInstance(Context context){
        if(mInstance==null){
            mInstance=new SharedPrefManger(context);
        }
        return mInstance;
    }
    public boolean userlogin(String name, String email){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString(KEY_NAME,name);
        editor.putString(KEY_EMAIL,email);

        editor.apply();

        return true;
    }
    public boolean isLogin(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_NAME,Context.MODE_PRIVATE);
        if(sharedPreferences.getString(KEY_EMAIL,null)!=null){
            return true;
        }
        return false; //沒登陸
    }
    public boolean logout(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }

    public String getUserName(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_NAME,null);
    }
    public String getUserEmail(){
        SharedPreferences sharedPreferences=mCtx.getSharedPreferences(SHARED_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL,null);
    }
}

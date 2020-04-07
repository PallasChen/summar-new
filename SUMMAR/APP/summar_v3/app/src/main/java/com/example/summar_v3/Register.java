package com.example.summar_v3;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity implements View.OnClickListener {
    EditText username,password,email;
    TextView birth;
    RadioButton male,female;
    Calendar cal;
    int year,month,day;
    RadioGroup gender;
    CheckBox checkBox;

    //存變數的地方
    String str_username =null;
    String str_password;
    String str_email = null;
    String str_gender =null;
    String str_birth =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = (EditText)findViewById(R.id.et_username);
        password=(EditText)findViewById(R.id.et_password);
        email=(EditText)findViewById(R.id.et_email);
        male=(RadioButton)findViewById(R.id.radioButton_male);
        female=(RadioButton)findViewById(R.id.radioButton_female);
        gender = (RadioGroup)findViewById(R.id.radio_group);

        birth = (TextView)findViewById(R.id.et_birth);
        birth.setOnClickListener(this);

        checkBox=(CheckBox)findViewById(R.id.checkBox);

        getDate();
    }
    //今天日期
    private void getDate() {
        cal=Calendar.getInstance();
        year=cal.get(Calendar.YEAR);       //获取年月日时分秒
        month=cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day=cal.get(Calendar.DAY_OF_MONTH);
    }

    //註冊鈕
    public void Onreg(View view){
        str_username = username.getText().toString();
        str_password = password.getText().toString().trim();
        str_email = email.getText().toString();
        str_gender ="";
        str_birth =birth.getText().toString() ;
        switch (gender.getCheckedRadioButtonId()){
                    case R.id.radioButton_male:
                        str_gender="male";
                        break;
                    case R.id.radioButton_female:
                        str_gender="female";
                        break;
                }

                String type="register";
        //email驗證
        String validemail="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Matcher matcher = Pattern.compile(validemail).matcher(str_email);
        if(str_username.isEmpty()||str_username.equals("")) {
            username.setError("用戶名不可為空");
        }
        else if(!matcher.matches()) {
            email.setError("請輸入有效的電子郵件");
        }
        else if(str_password.isEmpty()||str_password.equals("")||str_password.length()<6) {
            password.setError("請輸入密碼且至少六碼");
        }
        else if(!checkBox.isChecked()){
                        Toast.makeText(this,"請詳細閱讀並同意",Toast.LENGTH_SHORT).show();
                    }
        else{
                        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
                        backgroundWorker.execute(type, str_username, str_password, str_email, str_gender, str_birth);
                    }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.et_birth){
            DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker arg0, int y, int m, int d) {
                    year=y; month=m; day=d;
                    birth.setText(y+"-"+(++m)+"-"+d);

                    //将选择的日期显示到birth中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
                }
            };
            DatePickerDialog dialog=new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,listener,year,month,day);
            //后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
            //設定最大日期
            dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
            dialog.show();

        }
    }
}
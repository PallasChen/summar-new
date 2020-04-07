package com.example.summar_v3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.INVISIBLE;

public class personal extends AppCompatActivity implements View.OnClickListener{

    Button b1,b2,b3,b4,b5; //toolbar
    String useremail;
    JSONArray jsonArray;
    //個人頁面
    private Button btnSelect;
    private ImageView ivImage;
    private ImageButton btnsetting;
    private ImageView image;
    String username; //使用者名稱
    TextView hint,name;
    List<String> articles=new ArrayList<>(); //存放文章列表的地方
    List<News> newsList=new ArrayList<>(); //

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String selectedImagePath; // 圖片檔案位置

    //收藏
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal);
        image = findViewById(R.id.imageView);
        btnSelect = findViewById(R.id.camera);
        ivImage =  findViewById(R.id.picture);
        btnsetting = findViewById(R.id.setting);//設定

        //使用者名稱
        username=SharedPrefManger.getInstance(this).getUserName();
        name=findViewById(R.id.textView);
        name.setText(username);


        //目前無收藏的文章
        hint=findViewById(R.id.textView4);

        //工具列
        b1=(Button)findViewById(R.id.bt1);
        b1.setOnClickListener(this);
        b2=(Button)findViewById(R.id.bt2);
        b2.setOnClickListener(this);
        b3=(Button)findViewById(R.id.bt3);
        b3.setOnClickListener(this);
        b4=(Button)findViewById(R.id.bt4);
        b4.setOnClickListener(this);
        b5=(Button)findViewById(R.id.bt5);
//        b5.setOnClickListener(this);
        b5.setBackgroundResource(R.drawable.personc);

        //email
        useremail=SharedPrefManger.getInstance(this).getUserEmail();

        //資料庫連線設定
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

        //取得使用者收藏列表
        getData_collect();
        //取得文章內容
        for(int i=0;i<articles.size();i++){
            getData(articles.get(i));
        }

        //收藏
        viewPager =findViewById(R.id.viewpager);

        final CollectAdapter collectAdapter = new CollectAdapter(this,newsList);

        viewPager.setAdapter(collectAdapter);

        //目前無收藏的文章
        if(collectAdapter.getCount()>0){hint.setVisibility(View.GONE);}

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });

        btnsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(personal.this, settingg.class);
                startActivity(intent);
            }
        });
        //設定頁面

        // 取出最後圖片檔案位置
        try {
            SharedPreferences preferencesGet = getApplicationContext()
                    .getSharedPreferences("image",
                            android.content.Context.MODE_PRIVATE);
            selectedImagePath = preferencesGet.getString("selectedImagePath",
                    ""); // 圖片檔案位置，預設為空

            Log.i("selectedImagePath", selectedImagePath + "");

        } catch (Exception e) {
        }

        /* 選擇照片 */
        btnSelect.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                selectImage();
            }
        });

        setImage();


    }

    /* 設定圖片 */
    private void setImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false; // 不顯示照片
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        /* 圖片縮小2倍 */
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE) {
            scale *= 2;
        }
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false; // 顯示照片
        Bitmap bm = BitmapFactory.decodeFile(selectedImagePath, options);
        Log.i("selectedImagePath", selectedImagePath + "");
        ivImage.setImageBitmap(bm);// 將圖片顯示
    }

    private void selectImage() {
        final String item1, item2, item3;
        item1 = "取用相機";
        item2 = "從圖庫選取";
        item3 = "取消";

        final CharSequence[] items = { item1, item2, item3 };

        AlertDialog.Builder builder = new AlertDialog.Builder(personal.this);
        builder.setTitle("新增照片視窗");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0: // 拍一張照
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//呼叫相機系統
                        startActivityForResult(intent, REQUEST_CAMERA);//不能被解釋為一個向量

                        break;
                    case 1: // 從圖庫選取
                        Intent intent1 = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent1.setType("image/*");
                        startActivityForResult(
                                Intent.createChooser(intent1, "選擇開啟圖庫"),
                                SELECT_FILE);
                        break;
                    default: // 取消
                        dialog.dismiss(); // 關閉對畫框
                        break;
                }

            }
        });
        builder.show();
    }

    /* 啟動選擇方式 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) { // 從圖庫開啟
                onSelectFromGalleryResult(data);
            }
            if (requestCode == REQUEST_CAMERA) { // 拍照
                onCaptureImageResult(data);
            }else if(requestCode==RESULT_CANCELED){
                Toast.makeText(personal.this,"取消拍照",Toast.LENGTH_SHORT).show();
            }
        }

    }

    /* 拍照 */
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg"); // 輸出檔案名稱
        selectedImagePath = destination + ""; // 輸出檔案位置
        FileOutputStream fo;
        try {
            destination.createNewFile(); // 建立檔案
            fo = new FileOutputStream(destination); // 輸出
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivImage.setImageBitmap(thumbnail); // 將圖片顯示
        image.setVisibility(View.GONE);

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = { MediaColumns.DATA };
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();

        selectedImagePath = cursor.getString(column_index); // 選擇的照片位置

        setImage(); // 設定圖片
        image.setVisibility(INVISIBLE);
    }

    /* 結束時 */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        /* 紀錄圖片檔案位置 */
        SharedPreferences preferencesSave = getApplicationContext()
                .getSharedPreferences("image",
                        android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesSave.edit();
        editor.putString("selectedImagePath", selectedImagePath); // 紀錄最後圖片位置
        editor.apply();

        Log.i("onDestroy", "onDestroy");
    }

    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size/2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }
    private void getData_collect() {
        try {
            String result=DBConnector.executeQuery("sql=Select * From summar.member where email='"+useremail+"'");
            JSONArray jsonArray=new JSONArray(result);
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonData = jsonArray.getJSONObject(0);
                String id = jsonData.getString("collection"); //udn_20190827_I_1,udn_20190827_I_2 用逗號分割
                String ids[] = id.split(",");
                for (int k=0;k<ids.length;k++) {
                    if(ids[k]==null || ids[k].equals("")||ids[k].isEmpty()){}
                    else{
                    articles.add(ids[k]);
                    }
                }

            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void getData(String id){
        try {
            String result=DBConnector.executeQuery("sql=Select * From summar.new where id='"+id+"'");
            jsonArray=new JSONArray(result);
            News news=null;
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                String Id=jsonData.getString("id");
                String pic = jsonData.getString("pic");
                String title=jsonData.getString("title");
                String sa=jsonData.getString("sa");
                String url = jsonData.getString("url");
                String classification = jsonData.getString("classification");
                String date = jsonData.getString("date");
                String keyword = jsonData.getString("keyword");

                //將資料存進News，跟List
                news=new News(Id,pic,title,sa,url,date,classification,keyword);
                newsList.add(news);
            }
        }catch (Exception e){
        }
    }


    public String key() {
        return "circle";
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.bt1:
                // code for button when user clicks buttonOne.
                intent.setClass(this, home.class);
                startActivity(intent);
                break;
            case R.id.bt2:
                // do your code
                intent.setClass(this, classification.class);
                startActivity(intent);
                break;
            case R.id.bt3:
                // do your code
                intent.setClass(this,Search.class);
                startActivity(intent);
//                startActivity(intent);
                break;
            case R.id.bt4:
                // do your code
                intent.setClass(this,following.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }
}

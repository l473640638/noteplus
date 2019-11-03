package com.example.notep;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Date;





@RequiresApi(api = Build.VERSION_CODES.N)
public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "EditActivity";

    MySql mySql;
    private Button btnSave;
    private Button btnBack;

    private EditText editTitle;
    private EditText editBody;
    private AlertDialog.Builder dialog;
    private Button btnUpcoming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
    }

    void init(){
        mySql = new MySql(this);
        btnBack = findViewById(R.id.button_back);
        btnSave = findViewById(R.id.button_save);
        editTitle = findViewById(R.id.edit_title);
        editBody = findViewById(R.id.edit_body);
        btnUpcoming = findViewById(R.id.btn_edit_menu_upcoming);
        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnUpcoming.setOnClickListener(this);
        Date date = new Date(System.currentTimeMillis());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            String title;
            String body;

            title = editTitle.getText().toString();
            body = editBody.getText().toString();
            if (!isShowIng()){
                if (!"".equals(title)||!"".equals(body)){
                    showDialog(title,body);
                    clearDialog();
                } else {
                    intentStart();
                }
            }
        }
        return false;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        String title;
        String body;
        title = editTitle.getText().toString();
        body = editBody.getText().toString();
        switch (v.getId()){
            case R.id.button_save:
                if (saveFunction(title,body)){
                    intentStart();
                }
                break;
            case R.id.button_back:
                if (!"".equals(title)||!"".equals(body)){
                    showDialog(title,body);
                    clearDialog();
                } else {
                    intentStart();
                }
                break;
            case R.id.btn_edit_menu_upcoming:
                break;
            default:
                break;
        }
    }

    void intentStart(){
        Intent intent = new Intent(EditActivity.this,MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    boolean saveFunction(String title, String body){

        boolean flag = true;
        if ("".equals(title)){
            Toast.makeText(this,"标题不能为空", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (title.length()>10){
            Toast.makeText(this,"标题过长", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (body.length()>200){
            Toast.makeText(this,"内容过长", Toast.LENGTH_SHORT).show();
            flag = false;
        }

        if(flag){
            SQLiteDatabase db;
            ContentValues values;
            db = mySql.getWritableDatabase();
            values = new ContentValues();
            values.put(MySql.TITLE,title);
            values.put(MySql.ARTICLE,body);
            db.insert(MySql.TABLE_NAME_RECORD,null,values);
            Toast.makeText(this,"保存成功", Toast.LENGTH_SHORT).show();
            db.close();
        }
        return flag;
    }

    void showDialog(final String title, final String body ){
        dialog = new AlertDialog.Builder(EditActivity.this);
        dialog.setTitle("提示");
        dialog.setMessage("是否保存当前编辑内容");
        dialog.setPositiveButton("保存",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveFunction(title, body);
                intentStart();
                    }
                });

        dialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intentStart();
                    }
                });
        dialog.show();
    }

    void clearDialog(){
        dialog = null;
    }

    boolean isShowIng() {
        if (dialog != null) {
            return true;
        } else {
            return false;
        }
    }

}

package com.example.notep;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class AmendActivity extends AppCompatActivity implements View.OnClickListener{

    private final static String TAG = "AmendActivity";

    MySql mySql;
    private Button btnSave;
    private Button btnBack;
    private TextView amendTime;
    private TextView amendTitle;
    private EditText amendBody;
    private Note note;
    private AlertDialog.Builder dialog;

    private Button btnUpcoming;
    private Button btnNotice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amend);
        init();

    }


    @Override
    public void onClick(View v) {
        String body;
        body = amendBody.getText().toString();
        switch (v.getId()){
            case R.id.button_save:
                if (updateFunction(body)){
                    intentStart();
                }
                break;
            case R.id.button_back:
                showDialog(body);
                clearDialog();
                break;

            case R.id.btn_edit_menu_upcoming:
                Log.i("AmendActivity","这是修改页面的待办按钮被点击");
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //当返回按键被按下
            if (!isShowIng()){
                showDialog(amendBody.getText().toString());
                clearDialog();
            }
        }
        return false;
    }

    /*
     * 初始化函数
     */
    @SuppressLint("SetTextI18n")
    void init(){
        mySql = new MySql(this);
        btnBack = findViewById(R.id.button_back);
        btnSave = findViewById(R.id.button_save);
        amendTitle = findViewById(R.id.amend_title);
        amendBody = findViewById(R.id.amend_body);


        btnUpcoming = findViewById(R.id.btn_edit_menu_upcoming);


        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        btnNotice.setOnClickListener(this);
        btnUpcoming.setOnClickListener(this);

        Intent intent = this.getIntent();
        if (intent!=null){

            note = new Note();

            note.setId(Integer.valueOf(intent.getStringExtra(MySql.ID)));
            note.setTitleName(intent.getStringExtra(MySql.TITLE));
            note.setTextBody(intent.getStringExtra(MySql.ARTICLE));
            amendTitle.setText(note.getTitleName());
            String str="";
            amendBody.setText(note.getTextBody());
        }
    }

    /*
     * 返回主界面
     */
    void intentStart(){
        Intent intent = new Intent(AmendActivity.this,MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    /*
     * 保存函数
     */
    boolean updateFunction(String body){

        SQLiteDatabase db;
        ContentValues values;

        boolean flag = true;
        if (body.length()>200){
            Toast.makeText(this,"内容过长", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if(flag){
            // update
            db = mySql.getWritableDatabase();
            values = new ContentValues();
            values.put(MySql.ARTICLE,body);

            db.update(MySql.TABLE_NAME_RECORD,values,MySql.ID +"=?",
                    new String[]{note.getId().toString()});
            Toast.makeText(this,"修改成功", Toast.LENGTH_SHORT).show();
            db.close();
        }
        return flag;
    }

    /*
     * 弹窗函数
     * @param title
     * @param body
     * @param createDate
     */
    void showDialog(final String body){
        dialog = new AlertDialog.Builder(AmendActivity.this);
        dialog.setTitle("提示");
        dialog.setMessage("是否保存当前编辑内容");
        dialog.setPositiveButton("保存",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateFunction(body);
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

    boolean isShowIng(){
        if (dialog!=null){
            return true;
        }else{
            return false;
        }
    }


}
